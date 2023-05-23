package command;

import api.ChessBoardWriteService;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class EndTurnCommand extends Command {

    @Override
    public boolean execute(ChessBoardWriteService chessBoardWriteService) {
        return chessBoardWriteService.tryToPass();
    }
}
