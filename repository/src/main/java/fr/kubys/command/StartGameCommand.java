package fr.kubys.command;

import fr.kubys.api.ChessBoardWriteService;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class StartGameCommand extends Command {
    @Override
    public void execute(ChessBoardWriteService chessBoardWriteService) {
        chessBoardWriteService.startGame();
    }

    @Override
    public String toString() {
        return "Start game";
    }
}
