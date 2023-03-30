package piece;

import java.util.Optional;
import java.util.Set;

public abstract class Pawn extends Piece {
    public Pawn(Color color, char typePiece) {
        super(color, typePiece);
    }

    public abstract String twoSquaresForward();
    public abstract String oneSquareForward();

    @Override
    public abstract boolean isPositionTheoricallyReachable(int x, int y, Optional<Color> color);

    @Override
    public abstract Set<String> squaresOnThePath(String squareToMoveOn);

    @Override
    public String toString() {
        return this.getColor().toString().toLowerCase() + " Pawn";
    }
}
