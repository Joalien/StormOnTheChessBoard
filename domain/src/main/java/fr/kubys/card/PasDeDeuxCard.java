package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

public class PasDeDeuxCard extends Card<PieceCardParam> {

    public PasDeDeuxCard() {
        super("Pas de deux",
                "Déplacez n'importe laquelle de vos pièces en lui faisant effectuer exactement le même mouvement que la pièce que vous venez de jouer. Cette seconde pièce déplacée peut prendre.",
                CardType.AFTER_TURN,
                PieceCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, PieceCardParam param) {
        if (param.piece() == null)
            throw new IllegalStateException("Missing required card parameter");
        if (param.piece().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn()))
            throw new CannotMoveThisColorException(param.piece().getColor());
        if (chessBoard.getOutOfTheBoardPieces().contains(param.piece()))
            throw new IllegalArgumentException("Piece is not on the board");
        if (param.piece().isKing())
            throw new IllegalArgumentException("Cannot move the King with this card");

        Position destination = computeDestination(chessBoard, param.piece());

        // Destination must be empty or contain an enemy piece
        chessBoard.at(destination).getPiece().ifPresent(target -> {
            if (target.getColor() == chessBoard.getCurrentTurn())
                throw new IllegalArgumentException("Cannot capture your own piece");
            if (target.isKing())
                throw new IllegalArgumentException("Cannot capture the King");
        });
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, PieceCardParam param) {
        Position destination = computeDestination(chessBoard, param.piece());
        chessBoard.fakeSquare(null, param.piece().getPosition());
        chessBoard.fakeSquare(param.piece(), destination);
        boolean check = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !check;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, PieceCardParam param) {
        Position destination = computeDestination(chessBoard, param.piece());
        chessBoard.move(param.piece(), destination);
    }

    private Position computeDestination(ChessBoard chessBoard, Piece piece) {
        Position lastMoveFrom = chessBoard.getLastMoveFrom();
        Piece lastMovedPiece = chessBoard.getLastMovedPiece();
        if (lastMoveFrom == null || lastMovedPiece == null)
            throw new IllegalStateException("No previous move to replicate");

        int deltaFile = lastMovedPiece.getPosition().getFile().getFileNumber() - lastMoveFrom.getFile().getFileNumber();
        int deltaRow = lastMovedPiece.getPosition().getRow().getRowNumber() - lastMoveFrom.getRow().getRowNumber();

        int destFile = piece.getPosition().getFile().getFileNumber() + deltaFile;
        int destRow = piece.getPosition().getRow().getRowNumber() + deltaRow;

        return Position.posToSquare(destFile, destRow);
    }
}
