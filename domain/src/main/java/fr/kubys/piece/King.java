package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Collections;
import java.util.Set;

public class King extends Piece implements Castlable {

    private boolean canCastle = true;

    private final Position startingPosition;
    private final Position kingSideCastleTarget;
    private final Position queenSideCastleTarget;
    private final Position kingSideRookPass;
    private final Position queenSideRookPass;

    public King(Color color) {
        super(color);
        Row homeRow = color.homeRow();
        startingPosition = Position.posToSquare(File.E, homeRow);
        kingSideCastleTarget = Position.posToSquare(File.G, homeRow);
        queenSideCastleTarget = Position.posToSquare(File.C, homeRow);
        kingSideRookPass = Position.posToSquare(File.F, homeRow);
        queenSideRookPass = Position.posToSquare(File.D, homeRow);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        if (getFile() == file && getRow() == row) return false;

        if (this.canCastle() && color == null && this.getPosition().equals(startingPosition)) {
            Position target = Position.posToSquare(file, row);
            if (target.equals(kingSideCastleTarget) || target.equals(queenSideCastleTarget)) return true;
        }

        return (getFile().distanceTo(file) <= 1) && (getRow().distanceTo(row) <= 1);
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        if (!super.isPositionTheoreticallyReachable(squareToMoveOn)) return Collections.emptySet();

        if (this.getPosition().equals(startingPosition)) {
            if (squareToMoveOn.equals(kingSideCastleTarget)) return Set.of(kingSideRookPass);
            if (squareToMoveOn.equals(queenSideCastleTarget)) return Set.of(queenSideRookPass);
        }
        return Collections.emptySet();
    }

    @Override
    public boolean isKing() {
        return true;
    }

    @Override
    public boolean canCastle() {
        return canCastle;
    }

    @Override
    public void cannotCastleAnymore() {
        canCastle = false;
    }
}