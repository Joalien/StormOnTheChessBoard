package fr.kubys.command;

import fr.kubys.api.ChessBoardWriteService;
import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class PlayCardCommand<T extends CardParam> extends Command {
    Card<T> card;
    T parameters; // FIXME make immutable

    @Override
    public void execute(ChessBoardWriteService chessBoardWriteService) {
        chessBoardWriteService.tryToPlayCard(card, parameters);
    }

    @Override
    public String toString() {
        return "Play card %s with parameters %s".formatted(card, parameters);
    }
}
