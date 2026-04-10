package fr.kubys.piece;

import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.core.Row;

import java.util.Optional;

public class BlackPawn extends Pawn {

    public BlackPawn() {
        super(Color.BLACK);
    }

    @Override
    protected Row startingRow() {
        return Row.Seven;
    }

    @Override
    public boolean isOnPromotionRow() {
        return getRow() == Row.One;
    }

    @Override
    public Optional<Position> twoSquaresForward() {
        return this.getRow().previous()
                .flatMap(Row::previous)
                .map(row -> Position.posToSquare(this.getFile(), row));
    }

    @Override
    public Optional<Position> oneSquareForward() {
        return this.getRow().previous()
                .map(row -> Position.posToSquare(this.getFile(), row));
    }
}
