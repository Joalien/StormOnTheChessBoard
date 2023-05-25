package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

import java.util.List;

public class HomeCard extends Card {

    private Piece piece;
    private Position positionToMoveOn;

    public HomeCard() {
        super("Maison", "Ramener l'une de vos pièces (pas un pion) sur l'une des cases où elle pouvait se trouver en début de partie. Vous pouvez même prendre ainsi une pièce adverse", CardType.REPLACE_TURN);
    }

    @Override
    protected void setupParams(List<?> params) {
        this.piece = (Piece) params.get(0);
        this.positionToMoveOn = (Position) params.get(1);
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (piece == null) throw new IllegalStateException();
        if (positionToMoveOn == null) throw new IllegalStateException();
        if (piece instanceof Pawn) throw new IllegalArgumentException("You cannot rollback a pawn!");
        if (piece.getColor() != isPlayedBy) throw new CannotMoveThisColorException(piece.getColor());
        boolean positionToMoveOnIsNotStartingPositionOfPiece = ChessBoard.createWithInitialState()
                .allyPieces(piece.getColor())
                .stream()
                .filter(piece1 -> piece1.getClass() == piece.getClass())
                .map(Piece::getPosition)
                .noneMatch(pos -> pos.equals(positionToMoveOn));
        if (positionToMoveOnIsNotStartingPositionOfPiece)
            throw new IllegalArgumentException("%s didn't start the game on square %s".formatted(piece, positionToMoveOn));

        Boolean positionToMoveOnHasSameColorPiece = chessBoard.at(positionToMoveOn)
                .getPiece()
                .map(Piece::getColor)
                .map(color -> color == piece.getColor())
                .orElse(false);
        if (positionToMoveOnHasSameColorPiece)
            throw new IllegalArgumentException("You cannot rollback on a square occupied by an ally piece");
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        return true; // FIXME
    }

    @Override
    protected void doAction(ChessBoard chessBoard) {
        chessBoard.at(positionToMoveOn).getPiece().ifPresent(chessBoard::removePieceFromTheBoard);
        chessBoard.move(piece, positionToMoveOn);
    }
}
