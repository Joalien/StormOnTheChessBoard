package fr.kubys.ai;

import fr.kubys.ai.card.BoardEvaluator;
import fr.kubys.ai.card.CardCandidateGenerators;
import fr.kubys.ai.card.CardParamSerializer;
import fr.kubys.ai.card.CardPlanner;
import fr.kubys.ai.card.ScoredCardPlay;
import fr.kubys.api.ChessBoardReadService;
import fr.kubys.card.CardType;
import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayCardWithImmutableParamCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Color;
import fr.kubys.repository.ChessBoardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Decides AI turns by jointly considering moves and cards. Move selection is delegated to a
 * wrapped {@link AiStrategy} (e.g. Stockfish, Material), so card play stacks on top of any
 * difficulty level. The chess game's state machine permits exactly four shapes of turn
 * (plus end-turn): a move alone, a {@code BEFORE_TURN} card followed by a move, a move
 * followed by an {@code AFTER_TURN} card, or a {@code REPLACE_TURN} card alone. This
 * strategy enumerates each shape, simulates the resulting board via
 * {@link ChessBoardRepository#simulate}, scores it from the AI's perspective with
 * {@link BoardEvaluator}, and emits the highest-scoring sequence.
 */
public class CardAwareStrategy implements AiStrategy {

    private static final Logger log = LoggerFactory.getLogger(CardAwareStrategy.class);

    private final ChessBoardRepository repository;
    private final AiStrategy moveStrategy;
    private final CardPlanner planner;

    public CardAwareStrategy(ChessBoardRepository repository, AiStrategy moveStrategy) {
        this.repository = repository;
        this.moveStrategy = moveStrategy;
        this.planner = new CardPlanner(repository, new CardCandidateGenerators());
    }

    @Override
    public List<Command> decideMove(Integer gameId, ChessBoardReadService boardState) {
        Color aiColor = boardState.getCurrentPlayer().getColor();
        int baseline = BoardEvaluator.evaluate(boardState, aiColor);

        log.trace("[AI Game {}] decideMove start: aiColor={} baseline={} moveStrategy={}",
                gameId, aiColor, baseline, moveStrategy.getClass().getSimpleName());

        List<TurnPlan> plans = new ArrayList<>();
        plans.addAll(moveAlonePlans(gameId, boardState, aiColor, baseline));
        plans.addAll(beforeThenMovePlans(gameId, boardState, aiColor, baseline));
        plans.addAll(moveThenAfterPlans(gameId, boardState, aiColor, baseline));
        plans.addAll(replaceTurnPlans(gameId, boardState, baseline));

        if (log.isTraceEnabled()) {
            plans.forEach(plan -> log.trace("[AI Game {}]   plan {} score={}", gameId, plan.label(), plan.score()));
        }

        Optional<TurnPlan> best = plans.stream().max(Comparator.comparingInt(TurnPlan::score));
        if (best.isEmpty()) {
            log.info("[AI Game {}] no candidate plan, passing", gameId);
            return List.of(EndTurnCommand.builder().gameId(gameId).build());
        }
        TurnPlan chosen = best.get();
        log.info("[AI Game {}] {} score={}", gameId, chosen.label(), chosen.score());
        List<Command> out = new ArrayList<>(chosen.commands());
        out.add(EndTurnCommand.builder().gameId(gameId).build());
        return out;
    }

    private Optional<PlayMoveCommand> findMove(Integer gameId, ChessBoardReadService board) {
        try {
            return moveStrategy.decideMove(gameId, board).stream()
                    .filter(PlayMoveCommand.class::isInstance)
                    .map(PlayMoveCommand.class::cast)
                    .findFirst();
        } catch (RuntimeException e) {
            log.trace("[AI Game {}]   move strategy failed: {}", gameId, e.getMessage());
            return Optional.empty();
        }
    }

    private List<TurnPlan> moveAlonePlans(Integer gameId, ChessBoardReadService board, Color perspective, int baseline) {
        return findMove(gameId, board)
                .map(move -> scorePlan(gameId, List.of(move), perspective, baseline,
                        "move %s→%s".formatted(move.getFrom(), move.getTo())))
                .stream().flatMap(Optional::stream).toList();
    }

    private List<TurnPlan> beforeThenMovePlans(Integer gameId, ChessBoardReadService board, Color perspective, int baseline) {
        Optional<ScoredCardPlay> best = planner.bestPlayFor(gameId, board, CardType.BEFORE_TURN, List::of);
        if (best.isEmpty()) return List.of();
        ScoredCardPlay play = best.get();
        Command cardCmd = toCommand(gameId, play);
        try {
            ChessBoardReadService afterCard = repository.simulate(gameId, List.of(cardCmd));
            return findMove(gameId, afterCard)
                    .map(move -> scorePlan(gameId, List.of(cardCmd, move), perspective, baseline,
                            "before-card %s + move %s→%s".formatted(play.card().getName(), move.getFrom(), move.getTo())))
                    .stream().flatMap(Optional::stream).toList();
        } catch (RuntimeException e) {
            return List.of();
        }
    }

    private List<TurnPlan> moveThenAfterPlans(Integer gameId, ChessBoardReadService board, Color perspective, int baseline) {
        Optional<PlayMoveCommand> moveOpt = findMove(gameId, board);
        if (moveOpt.isEmpty()) return List.of();
        PlayMoveCommand move = moveOpt.get();
        try {
            ChessBoardReadService afterMove = repository.simulate(gameId, List.of(move));
            return planner.bestPlayFor(gameId, afterMove, CardType.AFTER_TURN, () -> List.of(move))
                    .map(play -> {
                        Command cardCmd = toCommand(gameId, play);
                        return scorePlan(gameId, List.of(move, cardCmd), perspective, baseline,
                                "move %s→%s + after-card %s".formatted(move.getFrom(), move.getTo(), play.card().getName()));
                    })
                    .stream().flatMap(Optional::stream).toList();
        } catch (RuntimeException e) {
            return List.of();
        }
    }

    private List<TurnPlan> replaceTurnPlans(Integer gameId, ChessBoardReadService board, int baseline) {
        return planner.bestPlayFor(gameId, board, CardType.REPLACE_TURN, List::of)
                .map(play -> {
                    Command cardCmd = toCommand(gameId, play);
                    return scorePlan(gameId, List.of(cardCmd), board.getCurrentPlayer().getColor(), baseline,
                            "replace-card %s".formatted(play.card().getName()));
                })
                .stream().flatMap(Optional::stream).toList();
    }

    private Optional<TurnPlan> scorePlan(Integer gameId, List<Command> commands, Color perspective, int baseline, String label) {
        try {
            ChessBoardReadService simulated = repository.simulate(gameId, commands);
            int score = BoardEvaluator.evaluate(simulated, perspective) - baseline;
            return Optional.of(new TurnPlan(commands, score, label));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private static Command toCommand(Integer gameId, ScoredCardPlay play) {
        return PlayCardWithImmutableParamCommand.builder()
                .gameId(gameId)
                .cardName(play.card().getName())
                .param(CardParamSerializer.toMap(play.param()))
                .build();
    }

    private record TurnPlan(List<Command> commands, int score, String label) {
    }
}
