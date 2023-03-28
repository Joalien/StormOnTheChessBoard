import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class King extends Piece implements Castlable {

    private boolean hasMovedInThePast;
    private boolean cestLeRock = false;

    public King(Color color) {
        super(color, color == Color.WHITE ? 'K' : 'k');
    }

    //Penser à intégrer le rock !
    @Override
    public boolean reachableSquares(int x, int y, Optional<Color> color) {
        if (getX() == x && getY() == y) return false;

        boolean whiteCastle = this.getPosition().equals("e1") && this.color == Color.WHITE;
        boolean blackCastle = this.getPosition().equals("e8") && this.color == Color.BLACK;
        boolean whiteKingSideCastle = whiteCastle && BoardUtil.posToSquare(x, y).equals("g1");
        boolean whiteQueenSideCastle = whiteCastle && BoardUtil.posToSquare(x, y).equals("c1");
        boolean blackKingSideCastle = blackCastle && BoardUtil.posToSquare(x, y).equals("g8");
        boolean blackQueenSideCastle =blackCastle && BoardUtil.posToSquare(x, y).equals("c8");
        boolean canCastle = !hasMovedInThePast && (whiteKingSideCastle || whiteQueenSideCastle || blackKingSideCastle || blackQueenSideCastle);
        if (canCastle) return true;

        return (Math.abs(getX() - x) <= 1) && (Math.abs(getY() - y) <= 1);
    }

    @Override
    public Set<String> squaresOnThePath(String squareToMoveOn) {
        if (!super.reachableSquares(squareToMoveOn)) throw new IllegalArgumentException(squareToMoveOn);

        boolean whiteCastle = this.getPosition().equals("e1") && this.color == Color.WHITE;
        boolean blackCastle = this.getPosition().equals("e8") && this.color == Color.BLACK;
        boolean whiteKingSideCastle = whiteCastle && squareToMoveOn.equals("g1");
        boolean whiteQueenSideCastle = whiteCastle && squareToMoveOn.equals("c1");
        boolean blackKingSideCastle = blackCastle && squareToMoveOn.equals("g8");
        boolean blackQueenSideCastle =blackCastle && squareToMoveOn.equals("c8");

        if (whiteKingSideCastle) return Set.of("f1");
        else if (whiteQueenSideCastle) return Set.of("d1");
        else if (blackKingSideCastle) return Set.of("f8");
        else if (blackQueenSideCastle) return Set.of("d8");
        else return Collections.emptySet();
    }

    public boolean getCestLeRock() {
        return cestLeRock;
    }

    public void setCestLeRock(boolean encoreLeRock) {
        cestLeRock = encoreLeRock;
    }


    public boolean getHasMovedInThePast() {
        return hasMovedInThePast;
    }

    public void setHasMovedInThePast(boolean trool) {
        hasMovedInThePast = trool;
    }

    public boolean nothingOnThePath(int x, int y) {
        return true;
    }


}