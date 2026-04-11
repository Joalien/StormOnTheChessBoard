package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;

import java.util.Set;

public class ParatrooperCard extends Card<PieceToPositionCardParam> {

    private static final Set<Position> CENTER = Set.of(Position.d4, Position.e4, Position.d5, Position.e5);

    public ParatrooperCard() {
        super("Parachutiste",
                "Parachutez l'un des Pions que vous a pris votre adversaire sur l'une des quatre cases centrales de l'échiquier. Cette case doit être inoccupée.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null) throw new IllegalStateException("Missing required card parameter");
        if (!chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("Pawn must have been captured");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("Must be a Pawn");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("Must parachute your own captured Pawn");
        if (!CENTER.contains(param.positionToMoveOn()))
            throw new IllegalArgumentException("Target must be one of the four central squares");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("Target square must be empty");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.fakeSquare(param.piece(), param.positionToMoveOn());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeSquare(param.positionToMoveOn());
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
