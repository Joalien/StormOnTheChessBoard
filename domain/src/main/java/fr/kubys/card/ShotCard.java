package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.King;
import fr.kubys.piece.Piece;

public class ShotCard extends Card<PieceToPositionCardParam> {

    public ShotCard() {
        super("Tir",
                "Vous pouvez prendre une pièce adverse au tir, sans que son agresseur vienne prendre sa place. Le coup consiste à désigner une case que votre pièce pourrait atteindre, puis à retirer la pièce adverse qui s'y trouve.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null) throw new IllegalStateException();
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        Piece target = chessBoard.at(param.positionToMoveOn()).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("Target square is empty"));
        if (target.getColor() == chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Cannot shoot your own piece");
        if (target instanceof King)
            throw new IllegalArgumentException("Cannot shoot a King");
        if (!chessBoard.canAttack(param.piece(), param.positionToMoveOn()))
            throw new IllegalArgumentException("%s cannot reach %s".formatted(param.piece(), param.positionToMoveOn()));
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.fakeSquare(null, param.positionToMoveOn());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeSquare(param.positionToMoveOn());
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        Piece target = chessBoard.at(param.positionToMoveOn()).getPiece().orElseThrow();
        chessBoard.removePieceFromTheBoard(target);
    }
}
