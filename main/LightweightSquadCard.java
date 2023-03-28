public class LightweightSquadCard extends SCCard {

    private final Pawn pawn1;
    private final Pawn pawn2;

    public LightweightSquadCard(Pawn pawn1, Pawn pawn2) {
        this.pawn1 = pawn1;
        this.pawn2 = pawn2;
    }

    @Override
    public boolean play(ChessBoard chessBoard) {
        if (pawn1 == null) throw new IllegalStateException();
        if (pawn2 == null) throw new IllegalStateException();
        if (pawn1.equals(pawn2)) throw new IllegalArgumentException("You should select two different pawns");
        if (pawn1.getColor() != pawn2.getColor()) throw new IllegalArgumentException("You should move pawn of the same color");

        chessBoard.fakeSquare(pawn2.getPosition(), null);
        if (cannotMoveTwoSquaresForward(chessBoard, pawn1)) throw new IllegalArgumentException("You cannot move " + pawn1 + " two squares forward");
        chessBoard.unfakeSquare(pawn2.getPosition());
        chessBoard.fakeSquare(pawn1.getPosition(), null);
        if (cannotMoveTwoSquaresForward(chessBoard, pawn2)) throw new IllegalArgumentException("You cannot move " + pawn2 + " two squares forward");
        chessBoard.unfakeSquare(pawn1.getPosition());

        chessBoard.move(pawn1, twoSquaresForward(pawn1));
        chessBoard.move(pawn2, twoSquaresForward(pawn2));
        return true;
    }

    private boolean cannotMoveTwoSquaresForward(ChessBoard cb, Pawn pawn) {
        return cb.at(oneSquaresForward(pawn)).getPiece().isPresent() || cb.at(twoSquaresForward(pawn)).getPiece().isPresent();
    }

    private String twoSquaresForward(Pawn pawn) {
        if (pawn instanceof WhitePawn) return BoardUtil.posToSquare(pawn.getX(), pawn.getY() + 2);
        else return BoardUtil.posToSquare(pawn.getX(), pawn.getY() - 2);
    }

    private String oneSquaresForward(Pawn pawn) {
        if (pawn instanceof WhitePawn) return BoardUtil.posToSquare(pawn.getX(), pawn.getY() + 1);
        else return BoardUtil.posToSquare(pawn.getX(), pawn.getY() - 1);
    }
}
