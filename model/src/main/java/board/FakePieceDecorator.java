package board;

import piece.Color;
import piece.Piece;
import piece.Square;
import position.File;
import position.Position;
import position.Row;

import java.util.Optional;
import java.util.Set;

public class FakePieceDecorator extends Piece {

    private final Piece fakePiece;
    private final Square fakeSquare;

    public FakePieceDecorator(Piece originalPiece, Square fakeSquare) {
        super(originalPiece.getColor());
        this.fakePiece = originalPiece.clone(); // deep copy
        this.fakePiece.setSquare(fakeSquare);
        this.fakeSquare = fakeSquare;
    }

    @Override
    public File getFile() {
        return fakePiece.getFile();
    }

    @Override
    public Row getRow() {
        return fakePiece.getRow();
    }

    @Override
    public boolean isPositionTheoreticallyReachable(Position position) {
        return fakePiece.isPositionTheoreticallyReachable(position);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        return fakePiece.isPositionTheoreticallyReachable(file, row, color);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(Position position, Color color) {
        return super.isPositionTheoreticallyReachable(position, color);
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        return fakePiece.squaresOnThePath(squareToMoveOn);
    }

    @Override
    public Position getPosition() {
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
        return "Fake%s".formatted(fakePiece.getClass().getSimpleName());
    }

    @Override
    public Color getColor() {
        return fakePiece.getColor();
    }

    @Override
    public boolean isKing() {
        return fakePiece.isKing();
    }
}
