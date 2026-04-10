package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class NeutralPawn extends Pawn {

    public NeutralPawn() {
        super(Color.NONE);
    }

    @Override
    protected Row startingRow() {
        return null; // no two-square move
    }

    @Override
    public boolean isOnPromotionRow() {
        return getRow() == Row.One || getRow() == Row.Eight;
    }

    @Override
    public Optional<Position> oneSquareForward() {
        // Used by squaresOnThePath for two-square move; not applicable
        return getRow().next().map(row -> Position.posToSquare(getFile(), row));
    }

    @Override
    public Optional<Position> twoSquaresForward() {
        return Optional.empty(); // no two-square move
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        // Without movingAs context (check detection), accept both directions
        boolean reachableUp = checkDirection(file, row, color, getRow().next());
        boolean reachableDown = checkDirection(file, row, color, getRow().previous());
        return reachableUp || reachableDown;
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color targetPieceColor, Color effectiveColor) {
        if (effectiveColor == Color.WHITE) return checkDirection(file, row, targetPieceColor, getRow().next());
        if (effectiveColor == Color.BLACK) return checkDirection(file, row, targetPieceColor, getRow().previous());
        return isPositionTheoreticallyReachable(file, row, targetPieceColor);
    }

    private boolean checkDirection(File file, Row row, Color color, Optional<Row> forwardRow) {
        boolean moveOneSquare = forwardRow.map(r -> r == row).orElse(false);
        boolean moveForward = color == null && file == getFile() && moveOneSquare;
        boolean takePiece = moveOneSquare && getFile().distanceTo(file) == 1;
        boolean takeEnemyPiece = color != null && color != getColor() && takePiece;
        return moveForward || takeEnemyPiece;
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        return Collections.emptySet(); // no two-square move, no intermediate squares
    }
}
