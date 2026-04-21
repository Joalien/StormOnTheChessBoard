package fr.kubys.piece.extra;

import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Promotable;

import java.util.Set;

public class FusedPiece extends Piece implements Promotable {

    private final Piece first;
    private final Piece second;
    private final boolean canPromote;

    public FusedPiece(Piece first, Piece second) {
        super(first.getColor());
        this.first = first;
        this.second = second;
        this.canPromote = first instanceof Promotable || second instanceof Promotable;
    }

    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        first.setPosition(position);
        second.setPosition(position);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        return first.isPositionTheoreticallyReachable(file, row, color)
                || second.isPositionTheoreticallyReachable(file, row, color);
    }

    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color targetPieceColor, Color effectiveColor) {
        return first.isPositionTheoreticallyReachable(file, row, targetPieceColor, effectiveColor)
                || second.isPositionTheoreticallyReachable(file, row, targetPieceColor, effectiveColor);
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        if (first.isPositionTheoreticallyReachable(squareToMoveOn)) {
            return first.squaresOnThePath(squareToMoveOn);
        }
        return second.squaresOnThePath(squareToMoveOn);
    }

    @Override
    public boolean isOnPromotionRow() {
        if (first instanceof Promotable promotable) return promotable.isOnPromotionRow();
        if (second instanceof Promotable promotable) return promotable.isOnPromotionRow();
        return false;
    }

    public Piece getFirst() {
        return first;
    }

    public Piece getSecond() {
        return second;
    }

    @Override
    public String toString() {
        String colorStr = getColor().toString().toLowerCase();
        String firstName = first.getClass().getSimpleName();
        String secondName = second.getClass().getSimpleName();
        return "%s Fused(%s+%s)".formatted(colorStr, firstName, secondName);
    }

    @Override
    public Piece clone() {
        FusedPiece copy = new FusedPiece(first.clone(), second.clone());
        copyMetadataTo(copy);
        return copy;
    }
}
