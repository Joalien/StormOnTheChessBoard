public class Knight extends Piece {

    public Knight(int x, int y, boolean color, char lettre) {
        super(x, y, color, lettre);
    }

    public boolean reachableSquares(int x, int y) {
        boolean isNotOnTheSameLine = positionSurLigne != x;
        boolean isNotOnTheSameColumn = positionSurColonne != y;
        boolean distanceOfThree = Math.abs(positionSurLigne - x) + Math.abs(positionSurColonne - y) == 3;
        return isNotOnTheSameLine && isNotOnTheSameColumn && distanceOfThree;
    }

    //easy !!!!!
    public boolean nothingOnThePath(int x, int y) {
        return true;
    }
}