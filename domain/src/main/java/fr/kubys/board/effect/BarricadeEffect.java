package fr.kubys.board.effect;

import fr.kubys.core.Position;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class BarricadeEffect extends Effect {

    private final Set<Set<Position>> blockedPairs;
    private final List<List<Position>> edges;

    public BarricadeEffect(Position from1, Position to1, Position from2, Position to2) {
        super("Barricade");
        this.edges = List.of(List.of(from1, to1), List.of(from2, to2));
        this.blockedPairs = computeBlockedPairs(from1, to1, from2, to2);
    }

    public List<List<Position>> getEdges() {
        return edges;
    }

    @Override
    public List<Position> getPositions() {
        return edges.stream().flatMap(List::stream).toList();
    }

    @Override
    public boolean blocksPath(Position from, Position to, Set<Position> intermediates) {
        Set<Position> fullPath = new HashSet<>(intermediates);
        fullPath.add(from);
        fullPath.add(to);
        return blockedPairs.stream().anyMatch(fullPath::containsAll);
    }

    private static Set<Set<Position>> computeBlockedPairs(Position from1, Position to1, Position from2, Position to2) {
        Set<Set<Position>> pairs = new HashSet<>();
        // Orthogonal: each edge always blocks direct passage
        pairs.add(Set.of(from1, to1));
        pairs.add(Set.of(from2, to2));

        // Diagonal pairs depend on whether edges share a position (L-shaped) or not (straight)
        Position shared = findSharedPosition(from1, to1, from2, to2);
        if (shared != null) {
            // L-shaped: one diagonal blocked through the elbow
            Position other1 = from1.equals(shared) ? to1 : from1;
            Position other2 = from2.equals(shared) ? to2 : from2;
            int dFile = (other1.getFile().getFileNumber() - shared.getFile().getFileNumber())
                    + (other2.getFile().getFileNumber() - shared.getFile().getFileNumber());
            int dRow = (other1.getRow().getRowNumber() - shared.getRow().getRowNumber())
                    + (other2.getRow().getRowNumber() - shared.getRow().getRowNumber());
            Position diagonal = Position.posToSquare(
                    shared.getFile().getFileNumber() + dFile,
                    shared.getRow().getRowNumber() + dRow);
            pairs.add(Set.of(shared, diagonal));
        } else {
            // Straight: two diagonals crossing through the wall interior
            Position[] all = {from1, to1, from2, to2};
            for (int i = 0; i < all.length; i++) {
                for (int j = i + 1; j < all.length; j++) {
                    if (isDiagonallyAdjacent(all[i], all[j])
                            && !Set.of(all[i], all[j]).equals(Set.of(from1, to1))
                            && !Set.of(all[i], all[j]).equals(Set.of(from2, to2))) {
                        pairs.add(Set.of(all[i], all[j]));
                    }
                }
            }
        }

        return pairs;
    }

    private static Position findSharedPosition(Position from1, Position to1, Position from2, Position to2) {
        if (from1.equals(from2) || from1.equals(to2)) return from1;
        if (to1.equals(from2) || to1.equals(to2)) return to1;
        return null;
    }

    private static boolean isDiagonallyAdjacent(Position a, Position b) {
        return a.getFile().distanceTo(b.getFile()) == 1 && a.getRow().distanceTo(b.getRow()) == 1;
    }

    public static boolean areOrthogonallyAdjacent(Position a, Position b) {
        int fileDist = a.getFile().distanceTo(b.getFile());
        int rowDist = a.getRow().distanceTo(b.getRow());
        return (fileDist == 1 && rowDist == 0) || (fileDist == 0 && rowDist == 1);
    }

    public static boolean areEdgesConnected(Position from1, Position to1, Position from2, Position to2) {
        Set<Long> vertices1 = wallVertices(from1, to1);
        Set<Long> vertices2 = wallVertices(from2, to2);
        vertices1.retainAll(vertices2);
        return !vertices1.isEmpty();
    }

    private static Set<Long> wallVertices(Position a, Position b) {
        int f1 = Math.min(a.getFile().getFileNumber(), b.getFile().getFileNumber());
        int r1 = Math.min(a.getRow().getRowNumber(), b.getRow().getRowNumber());
        int f2 = Math.max(a.getFile().getFileNumber(), b.getFile().getFileNumber());
        int r2 = Math.max(a.getRow().getRowNumber(), b.getRow().getRowNumber());

        Set<Long> vertices = new HashSet<>();
        if (f2 == f1 + 1 && r1 == r2) {
            // Horizontal adjacency → vertical wall
            vertices.add(encodeVertex(2 * f1 + 1, 2 * r1 - 1));
            vertices.add(encodeVertex(2 * f1 + 1, 2 * r1 + 1));
        } else if (r2 == r1 + 1 && f1 == f2) {
            // Vertical adjacency → horizontal wall
            vertices.add(encodeVertex(2 * f1 - 1, 2 * r1 + 1));
            vertices.add(encodeVertex(2 * f1 + 1, 2 * r1 + 1));
        }
        return vertices;
    }

    private static long encodeVertex(int x, int y) {
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }
}
