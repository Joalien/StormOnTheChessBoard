package fr.kubys.command;

import fr.kubys.api.ChessBoardWriteService;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import fr.kubys.core.Position;

@SuperBuilder
@Getter
public class PlayMoveCommand extends Command {
    Position from;
    Position to;

    @Override
    public void execute(ChessBoardWriteService chessBoardWriteService) {
        chessBoardWriteService.tryToMove(from, to);
    }

    @Override
    public String toString() {
        return "from %s to %s".formatted(from, to);
    }
}
