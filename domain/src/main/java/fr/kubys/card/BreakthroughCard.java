package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

import java.util.Optional;

public class BreakthroughCard extends Card<PieceCardParam> {

    public BreakthroughCard() {
        super("Percée",
                "L'un de vos pions prend une pièce adverse en avançant droit devant lui.",
                CardType.REPLACE_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException();
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("You can only use a Pawn");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        Position target = oneForward(param.piece());
        if (target == null)
            throw new IllegalArgumentException("Pawn cannot advance");
        Piece targetPiece = chessBoard.at(target).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("No enemy piece in front of the pawn"));
        if (targetPiece.getColor() == chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Cannot capture your own piece");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        Position target = oneForward(param.piece());
        chessBoard.fakeSquare(param.piece(), target);
        chessBoard.fakeSquare(null, param.piece().getPosition());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        Position target = oneForward(param.piece());
        chessBoard.at(target).getPiece().ifPresent(p -> chessBoard.removePieceFromTheBoard(p));
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), target);
    }

    private Position oneForward(Piece piece) {
        Optional<Row> next = (piece.getColor() == Color.WHITE) ?
                piece.getRow().next() : piece.getRow().previous();
        return next.map(row -> Position.posToSquare(piece.getFile(), row)).orElse(null);
    }
}
