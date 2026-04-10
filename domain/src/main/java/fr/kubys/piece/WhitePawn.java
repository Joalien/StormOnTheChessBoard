package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Optional;

public class WhitePawn extends Pawn {

    public WhitePawn() {
        super(Color.WHITE);
    }

    @Override
    protected Row startingRow() {
        return Row.Two;
    }

    @Override
    public boolean isOnPromotionRow() {
        return getRow() == Row.Eight;
    }

    @Override
    public Optional<Position> twoSquaresForward() {
        return this.getRow().next()
                .flatMap(Row::next)
                .map(row -> Position.posToSquare(this.getFile(), row));
    }

    @Override
    public Optional<Position> oneSquareForward() {
        return this.getRow().next()
                .map(row -> Position.posToSquare(this.getFile(), row));
    }
}
