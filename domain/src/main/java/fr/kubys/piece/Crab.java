package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class Crab extends Pawn {

    public Crab(Color color) {
        super(color);
    }

    @Override
    protected Row startingRow() {
        return null;
    }

    @Override
    protected Row promotionRow() {
        return color == Color.WHITE ? Row.Eight : Row.One;
    }

    @Override
    public Optional<Position> oneSquareForward() {
        return Optional.empty();
    }

    @Override
    public Optional<Position> twoSquaresForward() {
        return Optional.empty();
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        if (getFile() == file && getRow() == row) return false;
        return getFile().distanceTo(file) == 1 && getRow().distanceTo(row) == 1;
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        return this.getColor().toString().toLowerCase() + " Crab";
    }
}
