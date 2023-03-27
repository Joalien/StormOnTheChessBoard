public class ChessBoard {
    private Piece board[][] = new Piece[9][9];

    public static ChessBoard createEmpty() {
        return new ChessBoard();
    }

    public static ChessBoard createWithInitialState() {
        ChessBoard chessBoard = new ChessBoard();

        chessBoard.getBoard()[5][1] = new King(5, 1, true, 'R');
        chessBoard.getBoard()[5][8] = new King(5, 8, false, 'r');
        chessBoard.getBoard()[4][1] = new Queen(4, 1, true, 'D');
        chessBoard.getBoard()[4][8] = new Queen(4, 8, false, 'd');
        chessBoard.getBoard()[3][1] = new Bishop(3, 1, true, 'F');
        chessBoard.getBoard()[6][1] = new Bishop(6, 1, true, 'F');
        chessBoard.getBoard()[6][8] = new Bishop(6, 8, false, 'f');
        chessBoard.getBoard()[2][1] = new Knight(2, 1, true, 'C');
        chessBoard.getBoard()[3][8] = new Bishop(3, 8, false, 'f');
        chessBoard.getBoard()[7][1] = new Knight(7, 1, true, 'C');
        chessBoard.getBoard()[2][8] = new Knight(2, 8, false, 'c');
        chessBoard.getBoard()[7][8] = new Knight(7, 8, false, 'c');
        chessBoard.getBoard()[1][1] = new Rock(1, 1, true, 'T');
        chessBoard.getBoard()[8][1] = new Rock(8, 1, true, 'T');
        chessBoard.getBoard()[1][8] = new Rock(1, 8, false, 't');
        chessBoard.getBoard()[8][8] = new Rock(8, 8, false, 't');
        chessBoard.getBoard()[1][2] = new Pawn(1, 2, true, 'P');
        chessBoard.getBoard()[2][2] = new Pawn(2, 2, true, 'P');
        chessBoard.getBoard()[3][2] = new Pawn(3, 2, true, 'P');
        chessBoard.getBoard()[4][2] = new Pawn(4, 2, true, 'P');
        chessBoard.getBoard()[5][2] = new Pawn(5, 2, true, 'P');
        chessBoard.getBoard()[6][2] = new Pawn(6, 2, true, 'P');
        chessBoard.getBoard()[7][2] = new Pawn(7, 2, true, 'P');
        chessBoard.getBoard()[8][2] = new Pawn(8, 2, true, 'P');
        chessBoard.getBoard()[1][7] = new Pawn(1, 7, false, 'p');
        chessBoard.getBoard()[2][7] = new Pawn(2, 7, false, 'p');
        chessBoard.getBoard()[3][7] = new Pawn(3, 7, false, 'p');
        chessBoard.getBoard()[4][7] = new Pawn(4, 7, false, 'p');
        chessBoard.getBoard()[5][7] = new Pawn(5, 7, false, 'p');
        chessBoard.getBoard()[6][7] = new Pawn(6, 7, false, 'p');
        chessBoard.getBoard()[7][7] = new Pawn(7, 7, false, 'p');
        chessBoard.getBoard()[8][7] = new Pawn(8, 7, false, 'p');
        Empty empty = new Empty(0, 0, '-');
        for (int row = 3; row <= 6; row++) {
            for (int column = 1; column <= 8; column++) {
                chessBoard.getBoard()[column][row] = chessBoard.getBoard()[0][0];
            }
        }
        return chessBoard;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void add(Piece piece, String square) {
        this.board[BoardUtil.getX(square)][BoardUtil.getY(square)] = piece;
    }
}
