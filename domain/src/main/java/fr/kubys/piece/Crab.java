package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Collections;
import java.util.Set;

public class Crab extends Piece implements Promotable {

    public Crab(Color color) {
        super(color);
    }

    @Override
    public boolean isOnPromotionRow() {
        return (color == Color.WHITE) ? getRow() == Row.Eight : getRow() == Row.One;
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
