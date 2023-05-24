package card;

import board.ChessBoard;
import lombok.extern.slf4j.Slf4j;
import piece.Knight;
import piece.Piece;
import piece.Queen;
import core.Position;

import java.util.List;

@Slf4j
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
    protected boolean doAction(ChessBoard chessBoard) {
        chessBoard.move(knight, positionToMoveOn);
        return true;
    }
}
