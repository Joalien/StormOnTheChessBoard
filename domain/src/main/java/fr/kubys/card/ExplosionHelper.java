package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;

import java.util.List;

public final class ExplosionHelper {

    private ExplosionHelper() {}

    /**
     * Returns all enemy pieces that the given piece can capture (excluding kings).
     * Bypasses board effects (e.g. ceasefire) because card effects can still remove pieces.
     */
    public static List<Piece> getCapturableEnemyPieces(ChessBoard chessBoard, Piece piece) {
        return Position.generateAllPositions().stream()
                .filter(pos -> !pos.equals(piece.getPosition()))
                .filter(pos -> piece.isPositionTheoreticallyReachable(pos, chessBoard.at(pos).getPiece().map(Piece::getColor).orElse(null)))
                .filter(pos -> hasEmptyPath(chessBoard, piece, pos))
                .flatMap(pos -> chessBoard.at(pos).getPiece().stream())
                .filter(p -> p.getColor() != piece.getColor())
                .filter(p -> !p.isKing())
                .toList();
    }

    private static boolean hasEmptyPath(ChessBoard chessBoard, Piece piece, Position target) {
        return piece.squaresOnThePath(target).stream()
                .allMatch(pos -> chessBoard.at(pos).getPiece().isEmpty());
    }

    /**
     * Checks whether exploding the given piece (removing it and all capturable enemy pieces)
     * would leave the player's own king in check via discovered attack.
     *
     * @return true if the explosion does NOT create check (safe to play)
     */
    public static boolean explosionDoesNotCreateCheck(ChessBoard chessBoard, Piece piece) {
        List<Piece> capturable = getCapturableEnemyPieces(chessBoard, piece);

        // Fake-remove the exploding piece
        chessBoard.fakeSquare(null, piece.getPosition());
        // Fake-remove all captured pieces
        for (Piece captured : capturable) {
            chessBoard.fakeSquare(null, captured.getPosition());
        }

        boolean isKingUnderAttack = chessBoard.isKingUnderAttack(chessBoard.getCurrentTurn());
        chessBoard.unfakeAllSquares();
        return !isKingUnderAttack;
    }
}
