package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

public class ImitatorCard extends Card<PieceToPositionCardParam> {

    public ImitatorCard() {
        super("Imitateur",
                "Déplacez n'importe laquelle de vos pièces (sauf un Pion) à la manière de la pièce que vient de jouer votre adversaire. Vous ne pouvez pas prendre ainsi une pièce adverse.",
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
        Piece lastMoved = chessBoard.getLastMovedPiece();
        if (lastMoved == null)
            throw new IllegalStateException("Aucun déplacement adverse n'a été effectué");
        // Create a temp piece of the same type as lastMoved to check movement
        Piece tempPiece = lastMoved.clone();
        tempPiece.setPosition(param.piece().getPosition());
        tempPiece.setColor(param.piece().getColor());
        if (!tempPiece.isPositionTheoreticallyReachable(param.positionToMoveOn()))
            throw new IllegalArgumentException("Ce déplacement n'est pas valide à la manière de la pièce adverse");
        if (!chessBoard.emptyPath(tempPiece, param.positionToMoveOn()))
            throw new IllegalArgumentException("Le chemin n'est pas libre");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
