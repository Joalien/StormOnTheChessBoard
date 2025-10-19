package fr.kubys.piece.extra;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Piece;

import java.util.Set;

public class BlackHolePiece extends Piece {
    public BlackHolePiece() {
        super(Color.NONE);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        return false;
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        throw new BlackHoleSquare.BlackHoleException("Black Hole cannot move!");
    }

    @Override
    public boolean isKing() {
        return false;
    }
}
