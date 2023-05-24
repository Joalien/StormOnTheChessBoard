package piece.extra;

import lombok.extern.slf4j.Slf4j;
import piece.Color;
import piece.Piece;
import piece.Square;
import position.File;
import position.Position;
import position.Row;

import java.util.Set;

@Slf4j
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
