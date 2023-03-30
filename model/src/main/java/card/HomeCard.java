package card;

import board.ChessBoard;
import piece.Pawn;
import piece.Piece;

public class HomeCard extends SCCard {

    private final Piece piece;
    private final String positionToMoveOn;

    public HomeCard(Piece piece, String positionToMoveOn) {
        super("Maison", "Ramener l'une de vos pièces (pas un pion) sur l'une des cases où elle pouvait se trouver en début de partie. Vous pouvez même prendre ainsi une pièce adverse", SCType.REPLACE_TURN);
        this.piece = piece;
        this.positionToMoveOn = positionToMoveOn;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (piece == null) throw new IllegalStateException();
        if (positionToMoveOn == null) throw new IllegalStateException();
        if (piece instanceof Pawn) throw new IllegalArgumentException("You cannot rollback a pawn!");
        boolean positionToMoveOnIsNotStartingPositionOfPiece = ChessBoard.createWithInitialState()
                .allyPieces(piece.getColor())
                .stream()
                .filter(piece1 -> piece1.getType() == piece.getType())
                .map(Piece::getPosition)
                .noneMatch(pos -> pos.equals(positionToMoveOn));
        if (positionToMoveOnIsNotStartingPositionOfPiece)
            throw new IllegalArgumentException(piece + " didn't start the game on square " + positionToMoveOn);

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
    protected boolean doAction(ChessBoard chessBoard) {
        chessBoard.at(positionToMoveOn).getPiece().ifPresent(chessBoard::removePieceFromTheBoard);
        chessBoard.move(piece, positionToMoveOn);

        return true;
    }
}
