package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.QuadrilleCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Square;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuadrilleCard extends Card<QuadrilleCardParam> {

    private static final List<Position> CORNERS = Stream.of(Position.a1, Position.h1, Position.h8, Position.a8).toList();

    public QuadrilleCard() {
        super("Quadrille", "Toutes les pièces se trouvant dans l'un des quatre coins de l'échiquier se déplacent d'un quart de tour dans le sens de votre choix. Les mouvements sont simultanés, il n'y a donc pas de prise.", CardType.AFTER_TURN, QuadrilleCardParam.class);
    }

    @Override
    protected void validInput(ChessBoard chessBoard, QuadrilleCardParam param) {
        if (param.direction() == null) throw new IllegalStateException();
    }

    @Override
    protected boolean doesNotCreateCheck(ChessBoard chessBoard, QuadrilleCardParam param) {
        Map<Position, Optional<Piece>> pieces = saveWhichPieceShouldGoInWhichCorner(chessBoard, param.direction());
        pieces.forEach((key, value) -> chessBoard.fakeSquare(value.orElse(null), key));
        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }

    @Override
    protected void doAction(ChessBoard chessBoard, QuadrilleCardParam param) {
        Map<Position, Optional<Piece>> pieces = saveWhichPieceShouldGoInWhichCorner(chessBoard, param.direction());
        removeCornersFromTheBoard(chessBoard);
        addPiecesInCorner(chessBoard, pieces);
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

    private Map<Position, Optional<Piece>> saveWhichPieceShouldGoInWhichCorner(ChessBoard chessBoard, Direction direction) {
        return CORNERS.stream()
                .map(chessBoard::at)
                .collect(Collectors.toMap(square -> direction.cornersMap.get(square.getPosition()), Square::getPiece));
    }

    public enum Direction {
        COUNTERCLOCKWISE(Map.of(
                Position.a1, Position.h1,
                Position.h1, Position.h8,
                Position.h8, Position.a8,
                Position.a8, Position.a1)),
        CLOCKWISE(Map.of(
                Position.h1, Position.a1,
                Position.h8, Position.h1,
                Position.a8, Position.h8,
                Position.a1, Position.a8));

        final Map<Position, Position> cornersMap;

        Direction(Map<Position, Position> cornersMap) {
            this.cornersMap = cornersMap;
        }
    }
}
