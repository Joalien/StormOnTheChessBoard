package fr.kubys.ai;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.*;
import fr.kubys.piece.extra.Crab;
import fr.kubys.piece.extra.FusedPiece;
import fr.kubys.piece.extra.Kangaroo;

import java.util.*;
import java.util.stream.Collectors;

public class MaterialStrategy implements AiStrategy {

    private final Random random;

    public MaterialStrategy() {
        this.random = new Random();
    }

    public MaterialStrategy(Random random) {
        this.random = random;
    }

    @Override
    public List<Command> decideMove(Integer gameId, ChessBoardReadService boardState) {
        return bestMove(boardState, random)
                .<List<Command>>map(move -> List.of(
                        PlayMoveCommand.builder().gameId(gameId).from(move.from()).to(move.to()).build(),
                        EndTurnCommand.builder().gameId(gameId).build()))
                .orElseGet(() -> List.of(EndTurnCommand.builder().gameId(gameId).build()));
    }

    /**
     * Selects the highest-scoring legal move for the player whose turn it currently is on the
     * supplied board. Ties are broken randomly via the supplied source of randomness. Returns
     * empty when the player has no legal moves.
     */
    public static Optional<ScoredMove> bestMove(ChessBoardReadService boardState, Random random) {
        List<ScoredMove> scored = scoreAllMoves(boardState);
        if (scored.isEmpty()) return Optional.empty();
        int bestScore = scored.stream().mapToInt(ScoredMove::score).max().orElseThrow();
        List<ScoredMove> bests = scored.stream().filter(m -> m.score() == bestScore).toList();
        return Optional.of(bests.get(random.nextInt(bests.size())));
    }

    public static List<ScoredMove> scoreAllMoves(ChessBoardReadService boardState) {
        Color aiColor = boardState.getCurrentPlayer().getColor();
        Map<Position, Piece> piecesByPosition = boardState.getPieces().stream()
                .collect(Collectors.toMap(Piece::getPosition, p -> p));
        Set<Position> enemyAttackedSquares = computeAttackedSquares(boardState, aiColor.opposite());

        return boardState.getPieces().stream()
                .filter(piece -> piece.getColor() == aiColor)
                .flatMap(piece -> boardState.getLegalMoves(piece.getPosition()).stream()
                        .map(target -> scoreMove(piece, target, piecesByPosition, enemyAttackedSquares)))
                .toList();
    }

    private static ScoredMove scoreMove(Piece piece, Position target, Map<Position, Piece> piecesByPosition, Set<Position> enemyAttackedSquares) {
        int score = 0;

        // Bonus for capturing an enemy piece
        Piece captured = piecesByPosition.get(target);
        if (captured != null && captured.getColor() != piece.getColor()) {
            score += pieceValue(captured) * 10;
        }

        // Penalty if destination is attacked by enemy
        if (enemyAttackedSquares.contains(target)) {
            score -= pieceValue(piece) * 10;
            // But capturing a more valuable piece while losing ours is still good
        }

        // Bonus if piece was on an attacked square and moves to safety
        if (enemyAttackedSquares.contains(piece.getPosition()) && !enemyAttackedSquares.contains(target)) {
            score += pieceValue(piece) * 5;
        }

        // Small bonus for central squares
        score += centerBonus(target);

        return new ScoredMove(piece.getPosition(), target, score);
    }

    private static Set<Position> computeAttackedSquares(ChessBoardReadService boardState, Color color) {
        return boardState.getPieces().stream()
                .filter(piece -> piece.getColor() == color)
                .flatMap(piece -> boardState.getLegalMoves(piece.getPosition()).stream())
                .collect(Collectors.toSet());
    }

    public static int pieceValue(Piece piece) {
        if (piece instanceof FusedPiece) return 7;
        if (piece instanceof Queen) return 9;
        if (piece instanceof Rock) return 5;
        if (piece instanceof Bishop) return 3;
        if (piece instanceof Knight) return 3;
        if (piece instanceof Kangaroo) return 3;
        if (piece instanceof Crab) return 3;
        if (piece instanceof Pawn) return 1;
        if (piece instanceof King) return 100;
        return 1;
    }

    private static int centerBonus(Position pos) {
        int file = pos.getFile().getFileNumber(); // A=1 .. H=8
        int row = pos.getRow().getRowNumber();    // 1..8
        // d4, d5, e4, e5 (file 4-5, row 4-5) = bonus 2; c3-f6 ring = bonus 1
        double fileDist = Math.abs(file - 4.5);
        double rowDist = Math.abs(row - 4.5);
        if (fileDist <= 1 && rowDist <= 1) return 2;
        if (fileDist <= 2 && rowDist <= 2) return 1;
        return 0;
    }

    public record ScoredMove(Position from, Position to, int score) {
    }
}
