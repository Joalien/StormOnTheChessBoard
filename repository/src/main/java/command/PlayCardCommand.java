package command;

import api.ChessBoardWriteService;
import card.Card;
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
    public boolean execute(ChessBoardWriteService chessBoardWriteService) {
        try {
            return chessBoardWriteService.tryToPlayCard((Card) Class.forName(cardName).getConstructor().newInstance(), parameters);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
