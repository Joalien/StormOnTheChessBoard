package fr.kubys.command;

import fr.kubys.api.ChessBoardWriteService;
import fr.kubys.card.Card;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@SuperBuilder
@Getter
public final class PlayCardCommand extends Command {
    Card card;
    Object parameters;

    @Override
    public void execute(ChessBoardWriteService chessBoardWriteService) {
        chessBoardWriteService.tryToPlayCard(card, parameters);
    }

    @Override
    public String toString() {
        return "Play card %s with parameters %s".formatted(card, parameters);
    }
}
