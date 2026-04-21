package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.King;
import fr.kubys.piece.Piece;

public class CannibalCard extends Card<PieceToPositionCardParam> {

    public CannibalCard() {
        super("Cannibale",
                "Prenez l'une de vos propres pièces avec une autre de celles-ci.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null) throw new IllegalStateException("Missing required card parameter");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        Piece target = chessBoard.at(param.positionToMoveOn()).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("Target square is empty"));
        if (target.getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("Target must be your own piece or a neutral piece");
        if (target instanceof King)
            throw new IllegalArgumentException("Cannot eat your King");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        Piece target = chessBoard.at(param.positionToMoveOn()).getPiece().orElseThrow();
        chessBoard.removePieceFromTheBoard(target);
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }
}
