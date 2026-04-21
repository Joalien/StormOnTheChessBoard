package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

public class FunnyCard extends Card<PieceToPositionCardParam> {

    public FunnyCard() {
        super("Rigolo",
                "L'un de vos Pions prend une pièce adverse en reculant, mais toujours en diagonale.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null) throw new IllegalStateException("Missing required card parameter");
        if (!(param.piece() instanceof Pawn))
            throw new IllegalArgumentException("You can only use a Pawn");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (param.piece().getFile().distanceTo(param.positionToMoveOn().getFile()) != 1)
            throw new IllegalArgumentException("Must move diagonally");
        if (param.piece().getRow().distanceTo(param.positionToMoveOn().getRow()) != 1)
            throw new IllegalArgumentException("Must move exactly one row");
        boolean isBackward = (param.piece().getColor() == Color.WHITE)
                ? param.positionToMoveOn().getRow().getRowNumber() < param.piece().getRow().getRowNumber()
                : param.positionToMoveOn().getRow().getRowNumber() > param.piece().getRow().getRowNumber();
        if (!isBackward)
            throw new IllegalArgumentException("Pawn must move backward");
        Piece target = chessBoard.at(param.positionToMoveOn()).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("Must capture an enemy piece"));
        if (target.getColor() == chessBoard.getCurrentTurn())
            throw new IllegalArgumentException("Cannot capture your own piece");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        Piece target = chessBoard.at(param.positionToMoveOn()).getPiece().orElseThrow();
        chessBoard.removePieceFromTheBoard(target);
        chessBoard.at(param.piece().getPosition()).removePiece();
        chessBoard.add(param.piece(), param.positionToMoveOn());
    }
}
