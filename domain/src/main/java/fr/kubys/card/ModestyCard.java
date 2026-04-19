package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Promotable;

public class ModestyCard extends Card<PieceToPositionCardParam> {

    public ModestyCard() {
        super("Modestie",
                "Déplacez n'importe laquelle de vos pièces à la manière d'un Pion. Si vous amenez ainsi une pièce sur la dernière rangée, vous pouvez la promouvoir en toute autre pièce de votre choix, comme un Pion.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Paramètre de carte manquant");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        Pawn tempPawn = new Pawn(param.piece().getColor());
        tempPawn.setPosition(param.piece().getPosition());
        if (!chessBoard.canAttack(tempPawn, param.positionToMoveOn()))
            throw new IllegalArgumentException("Ce déplacement n'est pas valide à la manière d'un Pion");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        return !chessBoard.doesMovingPieceCheckOurOwnKing(param.piece(), param.positionToMoveOn());
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.move(param.piece(), param.positionToMoveOn());
        // Non-Promotable pieces reaching last rank get replaced by a Pawn to trigger promotion
        Pawn tempPawn = new Pawn(param.piece().getColor());
        tempPawn.setPosition(param.positionToMoveOn());
        if (param.piece().findPosition().isPresent()
                && !(param.piece() instanceof Promotable)
                && tempPawn.isOnPromotionRow()) {
            chessBoard.removePieceFromTheBoard(param.piece());
            chessBoard.add(tempPawn, param.positionToMoveOn());
        }
    }
}
