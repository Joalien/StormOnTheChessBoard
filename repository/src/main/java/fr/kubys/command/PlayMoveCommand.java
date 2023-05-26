package fr.kubys.command;

import fr.kubys.api.ChessBoardWriteService;
import fr.kubys.core.Position;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

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
        return "Move %s to %s".formatted(from, to);
    }
}
