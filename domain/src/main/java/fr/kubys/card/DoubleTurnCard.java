package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Rock;

public class DoubleTurnCard extends Card<PieceToPositionCardParam> {

    public DoubleTurnCard() {
        super("Double Tour",
                "Si elle ne vient pas de prendre une pièce adverse, la Tour que vous avez jouée peut être déplacée de nouveau.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (!(param.piece() instanceof Rock))
            throw new IllegalArgumentException("Vous devez sélectionner une Tour");
        if (param.piece() != chessBoard.getLastMovedPiece())
            throw new IllegalArgumentException("Vous devez sélectionner la Tour que vous venez de jouer");
        if (lastMoveWasCapture(chessBoard))
            throw new IllegalArgumentException("La Tour vient de prendre une pièce, elle ne peut pas être redéplacée");
        if (!chessBoard.canAttack(param.piece(), param.positionToMoveOn()))
            throw new IllegalArgumentException("Ce déplacement n'est pas valide pour une Tour");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        return !chessBoard.doesMovingPieceCheckOurOwnKing(param.piece(), param.positionToMoveOn());
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }

    private boolean lastMoveWasCapture(ChessBoard chessBoard) {
        return chessBoard.lastMoveWasCapture();
    }
}
