package fr.kubys.ai.card;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.CardParam;
import fr.kubys.command.Command;
import fr.kubys.command.PlayCardWithImmutableParamCommand;
import fr.kubys.core.Color;
import fr.kubys.repository.ChessBoardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Picks the best (card, parameter) pair from the current player's hand for a given card type.
 * For each candidate, it asks the repository to simulate the resulting board, scores it via
 * {@link BoardEvaluator}, and returns the highest-scoring play (or empty if every candidate
 * was invalid or no card had a generator).
 */
public final class CardPlanner {

    private static final Logger log = LoggerFactory.getLogger(CardPlanner.class);

    private final ChessBoardRepository repository;
    private final CardCandidateGenerators generators;

    public CardPlanner(ChessBoardRepository repository, CardCandidateGenerators generators) {
        this.repository = repository;
        this.generators = generators;
    }

    public Optional<ScoredCardPlay> bestPlayFor(
            Integer gameId,
            ChessBoardReadService currentBoard,
            CardType type,
            Supplier<List<Command>> committedCommandsBeforeHypothetical
    ) {
        Color aiColor = currentBoard.getCurrentPlayer().getColor();
        int baseline = BoardEvaluator.evaluate(currentBoard, aiColor);

        Optional<ScoredCardPlay> best = currentBoard.getCurrentPlayer().getCards().stream()
                .filter(card -> card.getType() == type)
                .flatMap(card -> generators.candidatesFor(card, currentBoard).stream()
                        .map(param -> attemptScore(gameId, card, param, baseline, aiColor, committedCommandsBeforeHypothetical)))
                .flatMap(Optional::stream)
                .max(Comparator.comparingInt(ScoredCardPlay::score));

        if (log.isTraceEnabled()) {
            best.ifPresentOrElse(
                    play -> log.trace("[AI Game {}] {} best card: {} {} score={}", gameId, type, play.card().getName(), play.param(), play.score()),
                    () -> log.trace("[AI Game {}] {} no playable card found", gameId, type));
        }
        return best;
    }

    private Optional<ScoredCardPlay> attemptScore(
            Integer gameId,
            Card<? extends CardParam> card,
            CardParam param,
            int baseline,
            Color perspective,
            Supplier<List<Command>> committed
    ) {
        try {
            List<Command> hypothetical = new ArrayList<>(committed.get());
            hypothetical.add(PlayCardWithImmutableParamCommand.builder()
                    .gameId(gameId)
                    .cardName(card.getName())
                    .param(CardParamSerializer.toMap(param))
                    .build());
            ChessBoardReadService simulated = repository.simulate(gameId, hypothetical);
            int score = BoardEvaluator.evaluate(simulated, perspective) - baseline;
            log.trace("[AI Game {}]   candidate {} {} → score={}", gameId, card.getName(), param, score);
            return Optional.of(new ScoredCardPlay(card, param, score));
        } catch (RuntimeException invalidCandidate) {
            log.trace("[AI Game {}]   candidate {} {} rejected: {}", gameId, card.getName(), param, invalidCandidate.getMessage());
            return Optional.empty();
        }
    }
}
