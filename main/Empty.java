public class Empty extends Piece {

    public Empty(int x, int y, char lettre) {
        super(x, y, lettre);
    }

    public Empty(int x, int y, boolean color, char lettre) {
        super(x, y, color, lettre);
    }

    public char getType() {
        return type;
    }

   

    

    public boolean reachableSquares(int x, int y) {
        return false;
    }

    public boolean nothingOnThePath(int x, int y) {
        return false;
    }

    
}