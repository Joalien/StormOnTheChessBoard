import java.util.Optional;
import java.util.Set;

public abstract class Pawn extends Piece {
    public Pawn(Color color, char typePiece) {
        super(color, typePiece);
    }

    @Override
    protected abstract boolean reachableSquares(int x, int y, Optional<Color> color);

    @Override
    public abstract Set<String> squaresOnThePath(String squareToMoveOn);
}
