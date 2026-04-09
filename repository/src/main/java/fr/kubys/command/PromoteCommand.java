package fr.kubys.command;

import fr.kubys.api.ChessBoardService;
import fr.kubys.core.Position;
import fr.kubys.piece.PromotionPiece;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public final class PromoteCommand extends Command {
    Position position;
    PromotionPiece piece;

    @Override
    public void execute(ChessBoardService chessBoardService) {
        chessBoardService.promote(position, piece);
    }

    @Override
    public String toString() {
        return "Promote %s to %s".formatted(position, piece);
    }
}
