package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;

import java.util.Set;

public class CourtlyLoveCard extends Card<CourtlyLoveCard.CourtlyLoveCardParam> {

    public record CourtlyLoveCardParam(Knight knight, Position positionToMoveOn) {}
    private CourtlyLoveCardParam param;

    public CourtlyLoveCard() {
        super("Amour courtois", "Amenez l'un de vos cavaliers sur une case libre adjacente à votre dame", CardType.REPLACE_TURN);
    }

    @Override
    protected void setupParams(CourtlyLoveCardParam params) {
        this.param = params;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (param.knight == null) throw new IllegalStateException();
        if (param.positionToMoveOn == null) throw new IllegalStateException();
        if (param.knight.getColor() != isPlayedBy) throw new CannotMoveThisColorException(param.knight.getColor());
        boolean isNearbyQueen = chessBoard.allyPieces(param.knight.getColor()).stream()
                .filter(Queen.class::isInstance)
                .map(Piece::getPosition)
                .anyMatch(position -> position.areNearby(param.positionToMoveOn));
        if (!isNearbyQueen)
            throw new IllegalArgumentException("You should move %s nearby your queen".formatted(param.knight));
        if (chessBoard.at(param.positionToMoveOn).getPiece().isPresent())
            throw new IllegalArgumentException("You should move %s on an empty square".formatted(param.knight));

    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        chessBoard.fakeSquare(null, param.knight.getPosition());
        chessBoard.fakeSquare(param.knight, param.positionToMoveOn);
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(param.knight.getColor());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard) {
        chessBoard.move(param.knight, param.positionToMoveOn);
    }
}
