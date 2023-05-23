package piece;

import position.File;
import position.Position;
import position.Row;

import java.util.Optional;
import java.util.Set;

public abstract class Pawn extends Piece {
    protected Pawn(Color color, char typePiece) {
        super(color, typePiece);
    }

    public abstract Position twoSquaresForward();

    public abstract Position oneSquareForward();

    @Override
    public abstract boolean isPositionTheoreticallyReachable(File file, Row row, Optional<Color> color);

    @Override
    public abstract Set<Position> squaresOnThePath(Position squareToMoveOn);

    @Override
    public String toString() {
        return this.getColor().toString().toLowerCase() + " Pawn";
    }
}
