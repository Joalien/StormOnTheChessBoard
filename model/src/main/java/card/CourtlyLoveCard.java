package card;

import board.ChessBoard;
import lombok.extern.slf4j.Slf4j;
import piece.Knight;
import piece.Piece;
import piece.Queen;
import position.PositionUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CourtlyLoveCard extends SCCard {

    private final Knight knight;
    private final String positionToMoveOn;

    public CourtlyLoveCard(Knight knight, String positionToMoveOn) {
        super("Amour courtois", "Amenez l'un de vos cavaliers sur une case libre adjacente à votre dame");
        this.knight = knight;
        this.positionToMoveOn = positionToMoveOn;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (knight == null) throw new IllegalStateException();
        if (positionToMoveOn == null) throw new IllegalStateException();
        boolean isNearbyQueen = chessBoard.allyPieces(knight.getColor()).stream()
                .peek(System.out::println)
                .filter(Queen.class::isInstance)
                .peek(System.out::println)
                .map(Piece::getPosition)
                .peek(System.out::println)
                .anyMatch(pos1 -> PositionUtil.areNearby(pos1, positionToMoveOn));
        if (!isNearbyQueen) throw new IllegalArgumentException(String.format("You should move %s nearby your queen", knight));
        if (chessBoard.at(positionToMoveOn).getPiece().isPresent()) throw new IllegalArgumentException(String.format("You should move %s on an empty square", knight));

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
