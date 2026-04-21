package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.Piece;

public class AmphetaminesCard extends Card<PieceToPositionCardParam> {

    public AmphetaminesCard() {
        super("Amphétamines",
                "S'il ne vient pas de prendre une pièce adverse, le Fou que vous avez joué peut être déplacé de nouveau.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Bishop))
            throw new IllegalArgumentException("Vous devez sélectionner un Fou");
        if (param.piece() != chessBoard.getLastMovedPiece())
            throw new IllegalArgumentException("Vous devez sélectionner le Fou que vous venez de jouer");
        if (lastMoveWasCapture(chessBoard))
            throw new IllegalArgumentException("Le Fou vient de prendre une pièce, il ne peut pas être redéplacé");
        if (!chessBoard.canAttack(param.piece(), param.positionToMoveOn()))
            throw new IllegalArgumentException("Ce déplacement n'est pas valide pour un Fou");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }

    private boolean lastMoveWasCapture(ChessBoard chessBoard) {
        return chessBoard.lastMoveWasCapture();
    }
}
