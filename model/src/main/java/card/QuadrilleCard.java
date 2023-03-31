package card;

import board.ChessBoard;
import piece.Color;
import piece.Piece;
import piece.Square;

import java.util.*;
import java.util.stream.Collectors;

public class QuadrilleCard extends SCCard {

    private static final List<String> CORNERS = List.of("a1", "h1", "h8", "a8");
    private final Map<String, Optional<Piece>> pieces = new HashMap<>();

    private final Direction direction;

    public QuadrilleCard(Direction direction) {
        super("Quadrille", "Toutes les pièces se trouvant dans l'un des quatre coins de l'échiquier se déplacent d'un quart de tour dans le sens de votre choix. Les mouvements sont simultanés, il n'y a donc pas de prise.", SCType.AFTER_TURN);
        this.direction = direction;
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (direction == null) throw new IllegalStateException();
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        Map<String, Optional<Piece>> pieces = saveWhichPieceShouldGoInWhichCorner(chessBoard);
        pieces.forEach((key, value) -> chessBoard.fakeSquare(value.orElse(null), key));
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(Color.WHITE);
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack; // FIXME color should depends on who plays the card
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        Map<String, Optional<Piece>> pieces = saveWhichPieceShouldGoInWhichCorner(chessBoard);
        removeCornersFromTheBoard(chessBoard);
        addPiecesInCorner(chessBoard, pieces);
        return true;
    }

    private static void addPiecesInCorner(ChessBoard chessBoard, Map<String, Optional<Piece>> pieces) {
        pieces.entrySet().stream()
                .filter(p -> p.getValue().isPresent())
                .forEach(optionalStringEntry -> chessBoard.add(optionalStringEntry.getValue().get(), optionalStringEntry.getKey()));
    }

    private static void removeCornersFromTheBoard(ChessBoard chessBoard) {
        CORNERS.stream()
                .map(chessBoard::at)
                .map(Square::getPiece)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(chessBoard::removePieceFromTheBoard);
    }

    private Map<String, Optional<Piece>> saveWhichPieceShouldGoInWhichCorner(ChessBoard chessBoard) {
        return CORNERS.stream()
                .map(chessBoard::at)
                .collect(Collectors.toMap(square -> direction.cornersMap.get(square.getPosition()), Square::getPiece));
    }

    enum Direction {
        COUNTERCLOCKWISE(Map.of(
                "a1", "h1",
                "h1", "h8",
                "h8", "a8",
                "a8", "a1")),
        CLOCKWISE(Map.of(
                "h1", "a1",
                "h8", "h1",
                "a8", "h8",
                "a1", "a8"));

        final Map<String, String> cornersMap;

        Direction(Map<String, String> cornersMap) {
            this.cornersMap = cornersMap;
        }
    }
}