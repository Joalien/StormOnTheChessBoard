package board;

import piece.Color;
import piece.Piece;
import piece.Square;

import java.util.Optional;
import java.util.Set;

public class FakePieceDecorator extends Piece {

    private final Piece piece;
    private final Square fakeSquare;

    public FakePieceDecorator(Piece piece, Square fakeSquare) {
        super(piece.getColor(), piece.getType());
        this.piece = piece;
        this.fakeSquare = fakeSquare;
    }

    @Override
    public Optional<Square> getSquare() {
        return Optional.of(fakeSquare);
    }

    @Override
    public void setSquare(Square square) {
        piece.setSquare(square);
    }

    @Override
    public int getY() {
        return piece.getY();
    }

    @Override
    public int getX() {
        return piece.getX();
    }


    @Override
    public Color getColor() {
        return piece.getColor();
    }

    @Override
    public char getType() {
        return piece.getType();
    }

    @Override
    public boolean isPositionTheoricallyReachable(int x, int y, Optional<Color> color) {
        return piece.isPositionTheoricallyReachable(x, y, color);
    }

    @Override
    public boolean isPositionTheoricallyReachable(String s) {
        return piece.isPositionTheoricallyReachable(s);
    }

    @Override
    public boolean isPositionTheoricallyReachable(String s, Optional<Color> color) {
        return piece.isPositionTheoricallyReachable(s, color);
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        return piece.squaresOnThePath(squareToMoveOn);
    }

    @Override
    public String getPosition() {
        return fakeSquare.getPosition();
    }

    @Override
    public String toString() {
        return String.format("Fake%s", piece.getClass().getSimpleName());
    }
}
