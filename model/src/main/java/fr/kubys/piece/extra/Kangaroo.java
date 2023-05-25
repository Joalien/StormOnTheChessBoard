package fr.kubys.piece.extra;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Square;

import java.util.Collections;
import java.util.Set;

public class Kangaroo extends Piece {

    public Kangaroo(Color color) {
        super(color);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        return Position.generateAllPositions().stream()
                .map(pos -> {
                    Knight knight = new Knight(this.color);
                    knight.setSquare(new Square(pos));
                    return knight;
                }).filter(knight -> knight.isPositionTheoreticallyReachable(file, row, color))
                .anyMatch(knight -> knight.isPositionTheoreticallyReachable(getFile(), getRow(), color));
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        return Collections.emptySet(); // piece.Knight can jump over pieces
    }

    @Override
    public boolean isKing() {
        return false;
    }
}