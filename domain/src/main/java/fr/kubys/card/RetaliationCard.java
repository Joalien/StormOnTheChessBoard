package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.PieceRemoval;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.piece.Pawn;

public class RetaliationCard extends Card<PieceCardParam> {

    public RetaliationCard() {
        super("Représailles",
                "Jouez cette carte lorsque votre adversaire vient de vous prendre une pièce (pas un Pion). Vous vous vengez alors en retirant l'un de ses pions de l'échiquier.",
                CardType.ENEMY_TURN_AFTER_MOVE,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null) throw new IllegalStateException("Missing required card parameter");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("You can only remove a Pawn");
        if (param.piece().getColor() != chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("You must target an enemy Pawn");
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("Pawn is not on the board");
        boolean nonPawnCapturedThisTurn = chessBoard.getPieceRemovals().stream()
                .filter(r -> r.reason() == PieceRemoval.RemovalReason.CAPTURED)
                .filter(r -> r.turn() == chessBoard.getTurnNumber())
                .filter(r -> r.piece().getColor() == chessBoard.getCurrentTurn().opposite())
                .anyMatch(r -> !(r.piece() instanceof Pawn));
        if (!nonPawnCapturedThisTurn)
            throw new IllegalStateException("Can only be played after a non-pawn piece was captured this turn");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        chessBoard.removePieceFromTheBoard(param.piece());
    }
}
