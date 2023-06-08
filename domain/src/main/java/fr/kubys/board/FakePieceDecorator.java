package fr.kubys.board;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Square;

import java.util.Optional;
import java.util.Set;

public class FakePieceDecorator extends Piece {

    private final Piece fakePiece;
    private final Position fakePosition;

    public FakePieceDecorator(Piece originalPiece, Position fakePosition) {
        super(originalPiece.getColor());
        this.fakePiece = originalPiece.clone(); // deep copy
        this.fakePiece.setPosition(fakePosition);
        this.fakePosition = fakePosition;
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
        return fakePosition;
    }

    @Override
    public void setPosition(Position position) {
        fakePiece.setPosition(position);
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
