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

    @Override
    public Optional<Square> getSquare() {
        return Optional.of(fakeSquare);
    }

    @Override
    public void setSquare(Square square) {
        throw new UnsupportedOperationException();
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
    public boolean reachableSquares(int x, int y, Optional<Color> color) {
        return piece.reachableSquares(x, y, color);
    }

    @Override
    public boolean reachableSquares(String s) {
        return piece.reachableSquares(s);
    }

    @Override
    public boolean reachableSquares(String s, Optional<Color> color) {
        return piece.reachableSquares(s, color);
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        return piece.squaresOnThePath(squareToMoveOn);
    }

    @Override
    public String getPosition() {
        return fakeSquare.getPosition();
    }
}
