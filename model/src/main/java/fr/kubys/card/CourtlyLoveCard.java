package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;


import java.util.List;

public class CourtlyLoveCard extends Card {

    private Knight knight;
    private Position positionToMoveOn;

    public CourtlyLoveCard() {
        super("Amour courtois", "Amenez l'un de vos cavaliers sur une case libre adjacente à votre dame", CardType.REPLACE_TURN);
    }

    @Override
    protected void setupParams(List<?> params) {
        this.knight = (Knight) params.get(0);
        this.positionToMoveOn = (Position) params.get(1);
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (knight == null) throw new IllegalStateException();
        if (positionToMoveOn == null) throw new IllegalStateException();
        if (knight.getColor() != isPlayedBy) throw new CannotMoveThisColorException(knight.getColor());
        boolean isNearbyQueen = chessBoard.allyPieces(knight.getColor()).stream()
                .filter(Queen.class::isInstance)
                .map(Piece::getPosition)
                .anyMatch(position -> position.areNearby(positionToMoveOn));
        if (!isNearbyQueen)
            throw new IllegalArgumentException("You should move %s nearby your queen".formatted(knight));
        if (chessBoard.at(positionToMoveOn).getPiece().isPresent())
            throw new IllegalArgumentException("You should move %s on an empty square".formatted(knight));

    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        chessBoard.fakeSquare(null, knight.getPosition());
        chessBoard.fakeSquare(knight, positionToMoveOn);
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(knight.getColor());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard) {
        chessBoard.move(knight, positionToMoveOn);
    }
}
