package fr.kubys.ai;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.*;
import fr.kubys.piece.extra.Crab;
import fr.kubys.piece.extra.FusedPiece;
import fr.kubys.piece.extra.Kangaroo;

import java.util.Map;
import java.util.stream.Collectors;

public class FenConverter {

    public static String toFen(ChessBoardReadService boardState) {
        Map<Position, Piece> piecesByPosition = boardState.getPieces().stream()
                .collect(Collectors.toMap(Piece::getPosition, p -> p));

        StringBuilder fen = new StringBuilder();

        // 1. Piece placement (rank 8 to rank 1)
        for (int rank = 8; rank >= 1; rank--) {
            int emptyCount = 0;
            for (int file = 1; file <= 8; file++) {
                Position pos = Position.posToSquare(
                        fr.kubys.core.File.fromNumber(file),
                        fr.kubys.core.Row.fromNumber(rank));
                Piece piece = piecesByPosition.get(pos);
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    char c = pieceToFenChar(piece);
                    if (c == '?') {
                        // Non-standard piece — skip it (treat as empty)
                        emptyCount++;
                    } else {
                        fen.append(piece.getColor() == Color.WHITE ? Character.toUpperCase(c) : Character.toLowerCase(c));
                    }
                }
            }
            if (emptyCount > 0) fen.append(emptyCount);
            if (rank > 1) fen.append('/');
        }

        // 2. Active color
        Color turn = boardState.getCurrentPlayer().getColor();
        fen.append(turn == Color.WHITE ? " w " : " b ");

        // 3. Castling availability
        String castling = computeCastling(boardState);
        fen.append(castling.isEmpty() ? "-" : castling);

        // 4. En passant (simplified — always "-")
        fen.append(" - ");

        // 5. Halfmove clock and fullmove number (defaults)
        fen.append("0 1");

        return fen.toString();
    }

    static char pieceToFenChar(Piece piece) {
        if (piece instanceof King) return 'k';
        if (piece instanceof Queen) return 'q';
        if (piece instanceof Rock) return 'r';
        if (piece instanceof Bishop) return 'b';
        if (piece instanceof Knight) return 'n';
        if (piece instanceof Pawn) return 'p';
        if (piece instanceof FusedPiece) return 'q'; // approximate as queen
        if (piece instanceof Kangaroo) return 'n'; // jumps like a knight
        if (piece instanceof Crab) return 'n'; // similar limited movement
        return '?'; // truly unknown — treated as empty in the FEN
    }

    private static String computeCastling(ChessBoardReadService boardState) {
        boolean whiteKingSide = false, whiteQueenSide = false;
        boolean blackKingSide = false, blackQueenSide = false;

        for (Piece piece : boardState.getPieces()) {
            if (piece instanceof King && piece.canCastle()) {
                if (piece.getColor() == Color.WHITE && piece.getPosition() == Position.e1) {
                    whiteKingSide = hasRookAt(boardState, Position.h1, Color.WHITE);
                    whiteQueenSide = hasRookAt(boardState, Position.a1, Color.WHITE);
                } else if (piece.getColor() == Color.BLACK && piece.getPosition() == Position.e8) {
                    blackKingSide = hasRookAt(boardState, Position.h8, Color.BLACK);
                    blackQueenSide = hasRookAt(boardState, Position.a8, Color.BLACK);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        if (whiteKingSide) sb.append('K');
        if (whiteQueenSide) sb.append('Q');
        if (blackKingSide) sb.append('k');
        if (blackQueenSide) sb.append('q');
        return sb.toString();
    }

    private static boolean hasRookAt(ChessBoardReadService boardState, Position pos, Color color) {
        return boardState.getPieces().stream()
                .anyMatch(p -> p instanceof Rock && p.getPosition() == pos && p.getColor() == color && p.canCastle());
    }

    public static boolean hasNonStandardPieces(ChessBoardReadService boardState) {
        return boardState.getPieces().stream().anyMatch(p -> pieceToFenChar(p) == '?');
    }
}
