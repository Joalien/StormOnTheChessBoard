package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Rock;

import java.util.List;

public class BombardCard extends Card<PieceToPositionCardParam> {

    public BombardCard() {
        super("Bombarde",
                "L'une de vos Tours peut sauter par dessus une pièce pour prendre la pièce adverse qui se trouve immédiatement au-delà.",
                CardType.AFTER_TURN,
                PieceToPositionCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceToPositionCardParam param) {
        if (param.piece() == null) throw new IllegalStateException();
        if (param.positionToMoveOn() == null) throw new IllegalStateException();
        if (!(param.piece() instanceof Rock))
            throw new IllegalArgumentException("You must select a Rook");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("The selected Rook must be on the board");

        Position from = param.piece().getPosition();
        Position to = param.positionToMoveOn();

        // Must be on same rank or file
        if (from.getFile() != to.getFile() && from.getRow() != to.getRow())
            throw new IllegalArgumentException("The Rook must move in a straight line");

        // Exactly one piece on the path (the piece to jump over)
        List<Position> piecesOnPath = param.piece().squaresOnThePath(to).stream()
                .filter(pos -> chessBoard.at(pos).getPiece().isPresent())
                .toList();
        if (piecesOnPath.size() != 1)
            throw new IllegalArgumentException("There must be exactly one piece to jump over");

        // Target must be immediately after the piece to jump over
        Position pieceToJumpOver = piecesOnPath.get(0);
        if (!isAdjacent(pieceToJumpOver, to))
            throw new IllegalArgumentException("Target must be immediately beyond the jumped piece");

        // Target must have an enemy piece
        Piece target = chessBoard.at(to).getPiece()
                .orElseThrow(() -> new IllegalArgumentException("Target square must contain an enemy piece"));
        if (target.getColor() == param.piece().getColor())
            throw new IllegalArgumentException("Cannot capture your own piece");
        if (target.isKing())
            throw new IllegalArgumentException("Cannot capture the King");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.fakeSquare(null, param.piece().getPosition());
        chessBoard.fakeSquare(param.piece(), param.positionToMoveOn());
        boolean check = chessBoard.isKingUnderAttack(param.piece().getColor());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceToPositionCardParam param) {
        chessBoard.move(param.piece(), param.positionToMoveOn());
    }

    private static boolean isAdjacent(Position a, Position b) {
        int fileDiff = Math.abs(a.getFile().getFileNumber() - b.getFile().getFileNumber());
        int rowDiff = Math.abs(a.getRow().getRowNumber() - b.getRow().getRowNumber());
        return (fileDiff == 0 && rowDiff == 1) || (fileDiff == 1 && rowDiff == 0);
    }
}
