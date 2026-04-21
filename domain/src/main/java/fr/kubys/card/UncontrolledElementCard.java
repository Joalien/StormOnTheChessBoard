package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Piece;

public class UncontrolledElementCard extends Card<PieceToPositionCardParam> {

    public UncontrolledElementCard() {
        super("Élément Incontrôlé",
                "Déplacez une pièce adverse selon ses règles normales de déplacement. Vous ne pouvez pas prendre ainsi une pièce adverse.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece().getColor() == chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Vous devez sélectionner une pièce adverse");
        if (!chessBoard.canAttack(param.piece(), param.positionToMoveOn()))
            throw new IllegalArgumentException("Ce déplacement n'est pas valide pour cette pièce");
        if (chessBoard.at(param.positionToMoveOn()).getPiece()
                .map(Piece::getColor)
                .map(c -> c != chessBoard.getCurrentTurn())
                .orElse(false))
            throw new IllegalArgumentException("Vous ne pouvez pas prendre une pièce adverse avec cette carte");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.tryToMove(param.piece(), param.positionToMoveOn());
    }
}
