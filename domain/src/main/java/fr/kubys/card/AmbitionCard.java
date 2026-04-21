package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;

public class AmbitionCard extends Card<PieceToPositionCardParam> {

    public AmbitionCard() {
        super("Ambition",
                "Remplacez l'un de vos pions par l'une des pièces, sauf la Dame, que vous a prises votre adversaire.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null) throw new IllegalStateException("Missing required card parameter");
        // piece = the captured piece to resurrect; positionToMoveOn = the pawn's position
        if (!chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("Piece must have been captured");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("Must resurrect one of your own captured pieces");
        if (param.piece() instanceof Queen)
            throw new IllegalArgumentException("Cannot resurrect a Queen");
        if (param.piece() instanceof Pawn)
            throw new IllegalArgumentException("Cannot resurrect a Pawn");
        Piece pawnOnBoard = chessBoard.at(param.positionToMoveOn()).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("No piece on target square"));
        if (!(pawnOnBoard instanceof Pawn))
            throw new IllegalArgumentException("Must replace a Pawn");
        if (pawnOnBoard.getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("Must replace your own Pawn");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        Piece pawn = chessBoard.at(param.positionToMoveOn()).getPiece().orElseThrow();
        chessBoard.removePieceFromTheBoard(pawn);
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
