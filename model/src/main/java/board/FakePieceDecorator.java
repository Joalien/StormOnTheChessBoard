package board;

import piece.Color;
import piece.Piece;
import piece.Square;

import java.util.Optional;
import java.util.Set;

public class FakePieceDecorator extends Piece {

    private final Piece fakePiece;
    private final Square fakeSquare;

    public FakePieceDecorator(Piece originalPiece, Square fakeSquare) {
        super(originalPiece.getColor(), originalPiece.getType());
        this.fakePiece = originalPiece.clone(); // deep copy
        this.fakePiece.setSquare(fakeSquare);
        this.fakeSquare = fakeSquare;
    }

    @Override
    public int getY() {
        return fakePiece.getY();
    }

    @Override
    public int getX() {
        return fakePiece.getX();
    }

    @Override
    public char getType() {
        return fakePiece.getType();
    }

    @Override
    public boolean isPositionTheoreticallyReachable(String s) {
        return fakePiece.isPositionTheoreticallyReachable(s);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(int x, int y, Optional<Color> color) {
        return fakePiece.isPositionTheoreticallyReachable(x, y, color);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(String s, Optional<Color> color) {
        return super.isPositionTheoreticallyReachable(s, color);
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        return fakePiece.squaresOnThePath(squareToMoveOn);
    }

    @Override
    public String getPosition() {
        return fakeSquare.getPosition();
    }

    @Override
    public Optional<Square> getSquare() {
        return Optional.of(fakeSquare);
    }

    @Override
    public void setSquare(Square square) {
        fakePiece.setSquare(square);
    }

    @Override
    public String toString() {
        return String.format("Fake%s", fakePiece.getClass().getSimpleName());
    }

    @Override
    public Color getColor() {
        return fakePiece.getColor();
    }
}
