import java.util.Optional;
import java.util.Set;

public abstract class Piece {

    protected Color color;
    protected char type;
    private Square square;

    public Piece(Color color, char typePiece) {
        this.color = color;
        this.type = typePiece;
    }

    public Optional<Square> getSquare() {
        return Optional.ofNullable(square);
    }

    public void setSquare(Square square) {
        this.square = square;
    }

    public int getY() {
        return BoardUtil.getY(square.getPosition());
    }

    public int getX() {
        return BoardUtil.getX(square.getPosition());
    }


    public Color getColor() {
        return color;
    }

    public char getType() {
        return this.type;
    }

    protected abstract boolean reachableSquares(int x, int y, Optional<Color> color);

    protected boolean reachableSquares(String s) {
        return reachableSquares(BoardUtil.getX(s), BoardUtil.getY(s), Optional.empty());
    }

    protected boolean reachableSquares(String s, Optional<Color> color) {
        return reachableSquares(BoardUtil.getX(s), BoardUtil.getY(s), color);
    }

    public abstract Set<String> squaresOnThePath(String squareToMoveOn);

    public String getPosition() {
        return this.getSquare().map(Square::getPosition).orElse(null);
    }
}