package fr.kubys.command;

import fr.kubys.api.ChessBoardService;
import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class PlayCardWithMutableParam<T extends CardParam> {
    Card<T> card;
    T parameters;

    public void execute(ChessBoardService chessBoardWriteService) {
        chessBoardWriteService.tryToPlayCard(card, parameters);
    }

    @Override
    public String toString() {
        return "Play card %s with parameters %s".formatted(card, parameters);
    }
}
