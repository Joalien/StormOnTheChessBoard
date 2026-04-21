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
        Color aiColor = boardState.getCurrentPlayer().getColor();
        Map<Position, Piece> piecesByPosition = boardState.getPieces().stream()
                .collect(Collectors.toMap(Piece::getPosition, p -> p));

        Set<Position> enemyAttackedSquares = computeAttackedSquares(boardState, aiColor.opposite());

        List<ScoredMove> scoredMoves = boardState.getPieces().stream()
                .filter(piece -> piece.getColor() == aiColor)
                .flatMap(piece -> boardState.getLegalMoves(piece.getPosition()).stream()
                        .map(target -> scoreMove(piece, target, piecesByPosition, enemyAttackedSquares)))
                .toList();

        if (scoredMoves.isEmpty()) {
            return List.of(EndTurnCommand.builder().gameId(gameId).build());
        }

        int bestScore = scoredMoves.stream().mapToInt(ScoredMove::score).max().orElseThrow();
        List<ScoredMove> bestMoves = scoredMoves.stream()
                .filter(m -> m.score() == bestScore)
                .toList();

        ScoredMove chosen = bestMoves.get(random.nextInt(bestMoves.size()));
        return List.of(
                PlayMoveCommand.builder().gameId(gameId).from(chosen.from()).to(chosen.to()).build(),
                EndTurnCommand.builder().gameId(gameId).build()
        );
    }

    private ScoredMove scoreMove(Piece piece, Position target, Map<Position, Piece> piecesByPosition, Set<Position> enemyAttackedSquares) {
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

    private Set<Position> computeAttackedSquares(ChessBoardReadService boardState, Color color) {
        return boardState.getPieces().stream()
                .filter(piece -> piece.getColor() == color)
                .flatMap(piece -> boardState.getLegalMoves(piece.getPosition()).stream())
                .collect(Collectors.toSet());
    }

    static int pieceValue(Piece piece) {
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

    private record ScoredMove(Position from, Position to, int score) {
    }
}
