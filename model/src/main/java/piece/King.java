package piece;

import position.File;
import position.Position;
import position.Row;

import java.util.Collections;
import java.util.Set;

import static position.Position.*;

public class King extends Piece implements Castlable {

    private boolean canCastle = true;

    public King(Color color) {
        super(color);
    }

    //Penser à intégrer le rock !
    @Override
    public boolean isPositionTheoreticallyReachable(File file, Row row, Color color) {
        if (getFile() == file && getRow() == row) return false;

        boolean whiteCastle = this.getPosition().equals(e1) && this.color == Color.WHITE;
        boolean blackCastle = this.getPosition().equals(e8) && this.color == Color.BLACK;
        boolean whiteKingSideCastle = whiteCastle && Position.posToSquare(file, row).equals(g1);
        boolean whiteQueenSideCastle = whiteCastle && Position.posToSquare(file, row).equals(c1);
        boolean blackKingSideCastle = blackCastle && Position.posToSquare(file, row).equals(g8);
        boolean blackQueenSideCastle = blackCastle && Position.posToSquare(file, row).equals(c8);
        boolean canCastle = this.canCastle() && (whiteKingSideCastle || whiteQueenSideCastle || blackKingSideCastle || blackQueenSideCastle) && color == null;
        if (canCastle) return true;

        return (getFile().distanceTo(file) <= 1) && (getRow().distanceTo(row) <= 1);
    }

    @Override
    public Set<Position> squaresOnThePath(Position squareToMoveOn) {
        if (!super.isPositionTheoreticallyReachable(squareToMoveOn)) return Collections.emptySet();

        boolean whiteCastle = this.getPosition().equals(e1) && this.color == Color.WHITE;
        boolean blackCastle = this.getPosition().equals(e8) && this.color == Color.BLACK;
        boolean whiteKingSideCastle = whiteCastle && squareToMoveOn.equals(g1);
        boolean whiteQueenSideCastle = whiteCastle && squareToMoveOn.equals(c1);
        boolean blackKingSideCastle = blackCastle && squareToMoveOn.equals(g8);
        boolean blackQueenSideCastle = blackCastle && squareToMoveOn.equals(c8);

        if (whiteKingSideCastle) return Set.of(f1);
        else if (whiteQueenSideCastle) return Set.of(d1);
        else if (blackKingSideCastle) return Set.of(f8);
        else if (blackQueenSideCastle) return Set.of(d8);
        else return Collections.emptySet();
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