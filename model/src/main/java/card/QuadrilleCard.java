package card;

import board.ChessBoard;
import piece.Color;
import piece.Piece;
import piece.Square;
import position.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static position.Position.*;

public class QuadrilleCard extends Card {

    private static final List<Position> CORNERS = Stream.of(a1, h1, h8, a8).toList();
    private final Map<Position, Optional<Piece>> pieces = new HashMap<>();

    private Direction direction;

    public QuadrilleCard() {
        super("Quadrille", "Toutes les pièces se trouvant dans l'un des quatre coins de l'échiquier se déplacent d'un quart de tour dans le sens de votre choix. Les mouvements sont simultanés, il n'y a donc pas de prise.", CardType.AFTER_TURN);
    }

    @Override
    protected void setupParams(List<?> params) {
        direction = (Direction) params.get(0);
    }

    @Override
    protected void validInput(ChessBoard chessBoard) {
        if (direction == null) throw new IllegalStateException();
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard) {
        Map<Position, Optional<Piece>> pieces = saveWhichPieceShouldGoInWhichCorner(chessBoard);
        pieces.forEach((key, value) -> chessBoard.fakeSquare(value.orElse(null), key));
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(isPlayedBy);
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected boolean doAction(ChessBoard chessBoard) {
        Map<Position, Optional<Piece>> pieces = saveWhichPieceShouldGoInWhichCorner(chessBoard);
        removeCornersFromTheBoard(chessBoard);
        addPiecesInCorner(chessBoard, pieces);
        return true;
    }

    private static void removeCornersFromTheBoard(ChessBoard chessBoard) {
        CORNERS.stream()
                .map(chessBoard::at)
                .map(Square::getPiece)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(chessBoard::removePieceFromTheBoard);
    }

    private static void addPiecesInCorner(ChessBoard chessBoard, Map<Position, Optional<Piece>> pieces) {
        pieces.entrySet().stream()
                .filter(p -> p.getValue().isPresent())
                .forEach(optionalStringEntry -> chessBoard.add(optionalStringEntry.getValue().get(), optionalStringEntry.getKey()));
    }

    private Map<Position, Optional<Piece>> saveWhichPieceShouldGoInWhichCorner(ChessBoard chessBoard) {
        return CORNERS.stream()
                .map(chessBoard::at)
                .collect(Collectors.toMap(square -> direction.cornersMap.get(square.getPosition()), Square::getPiece));
    }

    public enum Direction {
        COUNTERCLOCKWISE(Map.of(
                a1, h1,
                h1, h8,
                h8, a8,
                a8, a1)),
        CLOCKWISE(Map.of(
                h1, a1,
                h8, h1,
                a8, h8,
                a1, a8));

        final Map<Position, Position> cornersMap;

        Direction(Map<Position, Position> cornersMap) {
            this.cornersMap = cornersMap;
        }
    }
}
