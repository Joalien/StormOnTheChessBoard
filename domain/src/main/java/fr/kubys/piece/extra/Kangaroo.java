package fr.kubys.piece.extra;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Square;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Kangaroo extends Piece {

    public Kangaroo(Color color) {
        super(color);
    }

    public Predicate<Stream<Optional<Piece>>> hasEmptyPath() {
        return path -> path.anyMatch(Optional::isEmpty);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        return Position.generateAllPositions().stream()
            .map(pos -> {
                Knight knight = new Knight(this.color);
                knight.setPosition(pos);
                return knight;
            }).filter(knight -> knight.isPositionTheoreticallyReachable(file, row, color))
            .anyMatch(knight -> knight.isPositionTheoreticallyReachable(getFile(), getRow(), color));
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        return Position.generateAllPositions().stream()
            .map(pos -> {
                Knight knight = new Knight(this.color);
                knight.setPosition(pos);
                return knight;
            }).filter(knight -> knight.isPositionTheoreticallyReachable(squareToMoveOn, color))
            .filter(knight -> knight.isPositionTheoreticallyReachable(getFile(), getRow(), color))
            .map(Piece::getPosition)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isKing() {
        return false;
    }
}