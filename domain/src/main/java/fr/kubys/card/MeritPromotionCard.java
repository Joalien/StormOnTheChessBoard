package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Row;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Queen;

public class MeritPromotionCard extends Card<PieceCardParam> {

    public MeritPromotionCard() {
        super("Promotion au Mérite",
                "L'un de vos pions est promu en une pièce de votre choix, alors qu'il ne se trouve que sur la sixième rangée.",
                CardType.AFTER_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException();
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("You can only promote a Pawn");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        Row sixthRank = (chessBoard.getCurrentTurn() == Color.WHITE) ? Row.Six : Row.Three;
        if (param.piece().getRow() != sixthRank)
            throw new IllegalArgumentException("Pawn must be on the 6th rank");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        return true;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        var position = param.piece().getPosition();
        var color = param.piece().getColor();
        chessBoard.removePieceFromTheBoard(param.piece());
        chessBoard.add(new Queen(color), position);
    }
}
