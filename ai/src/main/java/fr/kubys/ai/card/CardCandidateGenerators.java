package fr.kubys.ai.card;

import fr.kubys.ai.MaterialStrategy;
import fr.kubys.api.ChessBoardReadService;
import fr.kubys.card.Card;
import fr.kubys.card.params.*;
import fr.kubys.core.Color;
import fr.kubys.core.File;
import fr.kubys.core.Position;
import fr.kubys.core.Row;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Registry of generic candidate generators keyed by {@link CardParam} subtype. Generators
 * intentionally over-produce: invalid parameters are filtered downstream when the card's
 * {@code validInput} throws during simulation.
 */
public final class CardCandidateGenerators {

    private static final int TWO_PIECE_TOP_K = 8;

    private final Map<Class<? extends CardParam>, CardCandidateGenerator> byParamType;

    public CardCandidateGenerators() {
        this.byParamType = new HashMap<>();
        register(NoCardParam.class, (card, board) -> List.of(new NoCardParam()));
        register(PieceCardParam.class, CardCandidateGenerators::pieceCandidates);
        register(PositionCardParam.class, CardCandidateGenerators::positionCandidates);
        register(PieceToPositionCardParam.class, CardCandidateGenerators::pieceToPositionCandidates);
        register(TwoPieceCardParam.class, CardCandidateGenerators::twoPieceCandidates);
        register(BarricadeCardParam.class, CardCandidateGenerators::barricadeCandidates);
        register(ChargeCardParam.class, CardCandidateGenerators::chargeCandidates);
    }

    private void register(Class<? extends CardParam> type, CardCandidateGenerator generator) {
        byParamType.put(type, generator);
    }

    public List<CardParam> candidatesFor(Card<? extends CardParam> card, ChessBoardReadService board) {
        CardCandidateGenerator generator = byParamType.get(card.getClazz());
        if (generator == null) return List.of();
        return generator.candidatesFor(card, board);
    }

    private static List<CardParam> pieceCandidates(Card<? extends CardParam> card, ChessBoardReadService board) {
        return board.getPieces().stream()
                .<CardParam>map(PieceCardParam::new)
                .toList();
    }

    private static List<CardParam> positionCandidates(Card<? extends CardParam> card, ChessBoardReadService board) {
        return Arrays.stream(Position.values())
                .<CardParam>map(PositionCardParam::new)
                .toList();
    }

    private static List<CardParam> pieceToPositionCandidates(Card<? extends CardParam> card, ChessBoardReadService board) {
        Color aiColor = board.getCurrentPlayer().getColor();
        return board.getPieces().stream()
                .filter(piece -> piece.getColor() == aiColor)
                .flatMap(piece -> board.getLegalMoves(piece.getPosition()).stream()
                        .<CardParam>map(target -> new PieceToPositionCardParam(piece, target)))
                .toList();
    }

    private static List<CardParam> twoPieceCandidates(Card<? extends CardParam> card, ChessBoardReadService board) {
        List<Piece> topPieces = board.getPieces().stream()
                .sorted(Comparator.comparingInt(MaterialStrategy::pieceValue).reversed())
                .limit(TWO_PIECE_TOP_K)
                .toList();
        List<CardParam> candidates = new ArrayList<>();
        for (int i = 0; i < topPieces.size(); i++) {
            for (int j = 0; j < topPieces.size(); j++) {
                if (i == j) continue;
                candidates.add(new TwoPieceCardParam(topPieces.get(i), topPieces.get(j)));
            }
        }
        return candidates;
    }

    private static List<CardParam> barricadeCandidates(Card<? extends CardParam> card, ChessBoardReadService board) {
        Color aiColor = board.getCurrentPlayer().getColor();
        Optional<Position> kingSquare = board.getPieces().stream()
                .filter(piece -> piece.getColor() == aiColor && piece.isKing())
                .map(Piece::getPosition)
                .findFirst();
        if (kingSquare.isEmpty()) return List.of();
        Set<Position> nearKing = squaresAround(kingSquare.get(), 2);
        List<Edge> edges = orthogonalEdges(nearKing);
        List<CardParam> candidates = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            for (int j = i + 1; j < edges.size(); j++) {
                Edge a = edges.get(i);
                Edge b = edges.get(j);
                candidates.add(new BarricadeCardParam(a.from(), a.to(), b.from(), b.to()));
            }
        }
        return candidates;
    }

    private static List<CardParam> chargeCandidates(Card<? extends CardParam> card, ChessBoardReadService board) {
        Color aiColor = board.getCurrentPlayer().getColor();
        Set<Pawn> ownPawns = board.getPieces().stream()
                .filter(piece -> piece.getColor() == aiColor)
                .filter(Pawn.class::isInstance)
                .map(Pawn.class::cast)
                .collect(Collectors.toSet());
        if (ownPawns.isEmpty()) return List.of(new ChargeCardParam(Set.of()));
        return Stream.<CardParam>concat(
                Stream.of(new ChargeCardParam(Set.of()), new ChargeCardParam(Set.copyOf(ownPawns))),
                ownPawns.stream().map(p -> new ChargeCardParam(Set.of(p)))
        ).toList();
    }

    private static Set<Position> squaresAround(Position center, int radius) {
        int file = center.getFile().getFileNumber();
        int row = center.getRow().getRowNumber();
        Set<Position> out = new HashSet<>();
        for (int df = -radius; df <= radius; df++) {
            for (int dr = -radius; dr <= radius; dr++) {
                int f = file + df;
                int r = row + dr;
                if (f < 1 || f > 8 || r < 1 || r > 8) continue;
                out.add(Position.valueOf(File.fromNumber(f).getFileName() + Row.fromNumber(r).getRowNumber()));
            }
        }
        return out;
    }

    private static List<Edge> orthogonalEdges(Set<Position> squares) {
        List<Edge> edges = new ArrayList<>();
        for (Position from : squares) {
            int f = from.getFile().getFileNumber();
            int r = from.getRow().getRowNumber();
            addEdgeIfValid(edges, from, f + 1, r, squares);
            addEdgeIfValid(edges, from, f, r + 1, squares);
        }
        return edges;
    }

    private static void addEdgeIfValid(List<Edge> edges, Position from, int targetFile, int targetRow, Set<Position> allowed) {
        if (targetFile < 1 || targetFile > 8 || targetRow < 1 || targetRow > 8) return;
        Position to = Position.valueOf(File.fromNumber(targetFile).getFileName() + Row.fromNumber(targetRow).getRowNumber());
        if (!allowed.contains(to)) return;
        edges.add(new Edge(from, to));
    }

    private record Edge(Position from, Position to) {
    }
}
