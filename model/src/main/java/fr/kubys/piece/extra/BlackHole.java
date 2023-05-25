package fr.kubys.piece.extra;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Square;

import java.util.Set;

public class BlackHole extends Piece {

    public BlackHole() {
        super(null);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        return false;
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        throw new BlackHoleException("You cannot move a black hole!");
    }

    @Override
    public void setSquare(Square square) {
        if (getSquare().isEmpty()) {
            super.setSquare(square);
        } else throw new BlackHoleException("You cannot move a black hole!");
    }

    @Override
    public boolean isKing() {
        return false;
    }

    public static class BlackHoleException extends RuntimeException {
        public BlackHoleException(String message) {
            super(message);
        }
    }
}
