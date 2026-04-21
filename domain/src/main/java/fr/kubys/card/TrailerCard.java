package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

import java.util.Optional;

public class TrailerCard extends Card<PieceToPositionCardParam> {

    public TrailerCard() {
        super("Remorque",
                "Le pion que vous déplacez (sans prise) tire la pièce qui se trouve juste derrière lui, de manière à ce qu'elle reste immédiatement derrière lui.",
                CardType.REPLACE_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null || param.positionToMoveOn() == null)
            throw new IllegalStateException("Missing required card parameter");
        if (!(param.piece() instanceof Pawn pawn))
            throw new IllegalArgumentException("You must select a Pawn");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("Pawn is not on the board");

        // Destination must be one or two squares forward and empty (no capture)
        boolean isOneForward = pawn.oneSquareForward()
                .filter(pos -> pos.equals(param.positionToMoveOn()))
                .isPresent();
        boolean isTwoForward = pawn.twoSquaresForward()
                .filter(pos -> pos.equals(param.positionToMoveOn()))
                .isPresent();
        if (!isOneForward && !isTwoForward)
            throw new IllegalArgumentException("Pawn must move one or two squares forward");
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("Destination must be empty (no capture allowed)");
        if (isTwoForward && pawn.oneSquareForward().flatMap(pos -> chessBoard.at(pos).getPiece()).isPresent())
            throw new IllegalArgumentException("Path is blocked");

        // There must be a piece behind the pawn
        Position behindPos = getBehindPosition(pawn)
                .orElseThrow(() -> new IllegalArgumentException("No square behind the pawn"));
        if (chessBoard.at(behindPos).getPiece().isEmpty())
            throw new IllegalArgumentException("There is no piece behind the pawn");
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        Pawn pawn = (Pawn) param.piece();
        Position behindPos = getBehindPosition(pawn).orElseThrow();
        Piece behindPiece = chessBoard.at(behindPos).getPiece().orElseThrow();
        Position behindNewPos = computeBehindNewPosition(pawn, param.positionToMoveOn());

        chessBoard.removePieceFromTheBoard(behindPiece);
        chessBoard.move(pawn, param.positionToMoveOn());
        chessBoard.add(behindPiece, behindNewPos);
    }

    private Position computeBehindNewPosition(Pawn pawn, Position pawnDest) {
        // Behind piece stays immediately behind the pawn at its new position
        Row behindRow = (pawn.getColor() == Color.WHITE)
                ? pawnDest.getRow().previous().orElseThrow()
                : pawnDest.getRow().next().orElseThrow();
        return Position.posToSquare(pawn.getFile(), behindRow);
    }

    private Optional<Position> getBehindPosition(Pawn pawn) {
        // Behind = opposite of forward direction
        Optional<Row> behindRow = (pawn.getColor() == Color.WHITE)
                ? pawn.getRow().previous()
                : pawn.getRow().next();
        return behindRow.map(row -> Position.posToSquare(pawn.getFile(), row));
    }
}
