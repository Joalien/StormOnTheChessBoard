package fr.kubys.ai.card;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;

import java.util.List;

@FunctionalInterface
public interface CardCandidateGenerator {

    /**
     * Returns a finite, small set of candidate parameters worth trying for the given card on
     * the given board. Implementations are expected to be conservative: invalid parameters
     * are filtered downstream by {@code Card.validInput} (which throws), so generating too
     * many candidates only costs simulation time.
     */
    List<CardParam> candidatesFor(Card<? extends CardParam> card, ChessBoardReadService board);
}
