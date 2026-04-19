package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Bishop;

public class PassingHallucinationCard extends Card<PieceToPositionCardParam> {

    public PassingHallucinationCard() {
        super("Hallucination passagère",
                "Déplacez n'importe laquelle de vos pièces à la manière d'un Fou. Vous ne pouvez pas prendre ainsi une pièce adverse.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("Vous ne pouvez pas prendre de pièce adverse avec cette carte");
        Bishop tempBishop = new Bishop(param.piece().getColor());
        tempBishop.setPosition(param.piece().getPosition());
        if (!chessBoard.canAttack(tempBishop, param.positionToMoveOn()))
            throw new IllegalArgumentException("Ce déplacement n'est pas valide à la manière d'un Fou");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        return !chessBoard.doesMovingPieceCheckOurOwnKing(param.piece(), param.positionToMoveOn());
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }
}
