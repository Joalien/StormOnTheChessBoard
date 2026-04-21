package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.CourtlyLoveCardParam;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;

public class CourtlyLoveCard extends Card<CourtlyLoveCardParam> {

    public CourtlyLoveCard() {
        super("Amour courtois", "Amenez l'un de vos cavaliers sur une case libre adjacente à votre dame", CardType.REPLACE_TURN, CourtlyLoveCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, CourtlyLoveCardParam param) {
        if (param.knight() == null) throw new IllegalStateException("Missing required card parameter");
        if (param.positionToMoveOn() == null) throw new IllegalStateException("Missing required card parameter");
        if (param.knight().getColor().cannotBeMovedBy(chessBoard.getCurrentTurn())) throw new CannotMoveThisColorException(param.knight().getColor());
        boolean isNearbyQueen = chessBoard.allyPieces(param.knight().getColor()).stream()
                .filter(Queen.class::isInstance)
                .map(Piece::getPosition)
                .anyMatch(position -> position.areNearby(param.positionToMoveOn()));
        if (!isNearbyQueen)
            throw new IllegalArgumentException("You should move %s nearby your queen".formatted(param.knight()));
        if (chessBoard.at(param.positionToMoveOn()).getPiece().isPresent())
            throw new IllegalArgumentException("You should move %s on an empty square".formatted(param.knight()));

    }

    @Override
    protected void doAction(ChessBoard chessBoard, CourtlyLoveCardParam param) {
        chessBoard.move(param.knight(), param.positionToMoveOn());
    }
}
