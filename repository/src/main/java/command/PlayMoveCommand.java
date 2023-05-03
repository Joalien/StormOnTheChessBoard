package command;

import api.ChessBoardWriteService;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class PlayMoveCommand extends Command {
    String from;
    String to;

    @Override
    public boolean execute(ChessBoardWriteService chessBoardWriteService) {
        try {
            return chessBoardWriteService.tryToMove(from, to);
        } catch (Exception e) {
            return false;
        }
    }
}
