package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Pawn;

public class FrightCard extends Card<PieceToPositionCardParam> {

    public FrightCard() {
        super("Frayeur",
                "Faites reculer un pion adverse d'une ou deux cases.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null) throw new IllegalStateException("Missing required card parameter");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("You can only push back a Pawn");
        if (!param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new IllegalArgumentException("You must target an enemy Pawn");
        if (param.piece().getFile() != param.positionToMoveOn().getFile())
            throw new IllegalArgumentException("Pawn must stay on the same file");
        int distance = param.piece().getRow().distanceTo(param.positionToMoveOn().getRow());
        if (distance < 1 || distance > 2)
            throw new IllegalArgumentException("Pawn must move back 1 or 2 squares");
        // Must move backward (toward its own side)
        boolean isBackward = (param.piece().getColor() == Color.WHITE)
                ? param.positionToMoveOn().getRow().getRowNumber() < param.piece().getRow().getRowNumber()
                : param.positionToMoveOn().getRow().getRowNumber() > param.piece().getRow().getRowNumber();
        if (!isBackward)
            throw new IllegalArgumentException("Pawn must move backward");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("Target square is not empty");
        if (chessBoard.getEffects().stream().anyMatch(e -> e.blocksPosition(param.positionToMoveOn())))
            throw new IllegalArgumentException("Target square is blocked by an effect");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.fakeSquare(param.piece(), param.positionToMoveOn());
        chessBoard.fakeSquare(null, param.piece().getPosition());
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
