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
    public boolean execute(ChessBoardWriteService chessBoardWriteService) {
        try {
            return chessBoardWriteService.tryToMove(from, to);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "from %s to %s".formatted(from, to);
    }
}
