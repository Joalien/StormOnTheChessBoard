package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Queen;

public class FavoriteCard extends Card<PieceToPositionCardParam> {

    public FavoriteCard() {
        super("Favorite",
                "Déplacez n'importe laquelle de vos pièces (sauf un Pion) à la manière d'une Dame. Vous ne pouvez pas prendre ainsi une pièce adverse.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece() instanceof Pawn)
            throw new IllegalArgumentException("Impossible de déplacer un Pion avec cette carte");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("Vous ne pouvez pas prendre de pièce adverse avec cette carte");
        Queen tempQueen = new Queen(param.piece().getColor());
        tempQueen.setPosition(param.piece().getPosition());
        if (!chessBoard.canAttack(tempQueen, param.positionToMoveOn()))
            throw new IllegalArgumentException("Ce déplacement n'est pas valide à la manière d'une Dame");
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
