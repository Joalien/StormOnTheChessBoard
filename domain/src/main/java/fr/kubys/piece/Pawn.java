package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Optional;
import java.util.Set;

public abstract class Pawn extends Piece {
    protected Pawn(Color color) {
        super(color);
    }

    public abstract Optional<Position> twoSquaresForward();

    public abstract Optional<Position> oneSquareForward();

    @Override
    public abstract boolean isPositionTheoreticallyReachable(File file, Row row, Color color);

    @Override
    public abstract Set<Position> squaresOnThePath(Position squareToMoveOn);

    @Override
    public String toString() {
        return this.getColor().toString().toLowerCase() + " Pawn";
    }

    @Override
    public boolean isKing() {
        return false;
    }
}
