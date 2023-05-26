package fr.kubys.command;

import fr.kubys.api.ChessBoardWriteService;
import fr.kubys.card.Card;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@SuperBuilder
@Getter
public class PlayCardCommand extends Command {
    String cardName;
    List<?> parameters;

    @Override
    public void execute(ChessBoardWriteService chessBoardWriteService) {
        try {
            chessBoardWriteService.tryToPlayCard((Card) Class.forName(cardName).getConstructor().newInstance(), parameters);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Play card %s with parameters %s".formatted(cardName, parameters);
    }
}
