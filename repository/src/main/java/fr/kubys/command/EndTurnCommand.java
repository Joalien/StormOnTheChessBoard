package fr.kubys.command;

import fr.kubys.api.ChessBoardWriteService;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class EndTurnCommand extends Command {

    @Override
    public void execute(ChessBoardWriteService chessBoardWriteService) {
        chessBoardWriteService.tryToPass();
    }

    @Override
    public String toString() {
        return "Pass turn";
    }
}
