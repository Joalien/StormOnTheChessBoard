package board;

import piece.Color;
import piece.Piece;
import position.Square;

import java.util.Optional;
import java.util.Set;

public class FakePiece extends Piece {

    private final Piece piece;
    private final Square fakeSquare;

    public FakePiece(Piece piece, Square fakeSquare) {
        super(piece.getColor(), piece.getType());
        this.piece = piece;
        this.fakeSquare = fakeSquare;
    }

    public Optional<Square> getSquare() {
        return Optional.of(fakeSquare);
    }

    public void setSquare(Square square) {
        throw new UnsupportedOperationException();
    }

    public int getY() {
        return piece.getY();
    }

    public int getX() {
        return piece.getX();
    }


    public Color getColor() {
        return piece.getColor();
    }

    public char getType() {
        return piece.getType();
    }

    public boolean reachableSquares(int x, int y, Optional<Color> color) {
        return piece.reachableSquares(x, y, color);
    }

    public boolean reachableSquares(String s) {
        return piece.reachableSquares(s);
    }

    public boolean reachableSquares(String s, Optional<Color> color) {
        return piece.reachableSquares(s, color);
    }

    public Set<String> squaresOnThePath(String squareToMoveOn) {
        return piece.squaresOnThePath(squareToMoveOn);
    }

    public String getPosition() {
        return fakeSquare.getPosition();
    }
}
