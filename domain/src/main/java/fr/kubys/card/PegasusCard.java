package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Knight;

public class PegasusCard extends Card<PieceToPositionCardParam> {

    public PegasusCard() {
        super("Pégase", "Déplacez l'un de vos Cavaliers en l'amenant directement sur n'importe quelle case libre de couleur opposée à celle sur laquelle il se trouve. Ce coup remplace votre déplacement normal.", CardType.REPLACE_TURN, PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null) throw new IllegalStateException();
        if (param.positionToMoveOn() == null) throw new IllegalStateException();
        if (!(param.piece() instanceof Knight))
            throw new IllegalArgumentException("Pegasus card can only move a Knight!");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());

        boolean sameColor = param.piece().getPosition().isWhiteSquare() == param.positionToMoveOn().isWhiteSquare();
        if (sameColor)
            throw new IllegalArgumentException("Target square must be of opposite color to the knight's current square");

        chessBoard.at(param.positionToMoveOn()).getPiece().ifPresent(piece -> {
            throw new IllegalArgumentException("Target square must be free");
        });
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.fakeSquare(null, param.piece().getPosition());
        chessBoard.fakeSquare(param.piece(), param.positionToMoveOn());
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(param.piece().getColor());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }
}
