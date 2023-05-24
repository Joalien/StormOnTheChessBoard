package position;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static position.File.*;
import static position.Row.*;

@Getter
public enum Position {
    h1(H, One), h2(H, Two), h3(H, Three), h4(H, Four), h5(H, Five), h6(H, Six), h7(H, Seven), h8(H, Height),
    g1(G, One), g2(G, Two), g3(G, Three), g4(G, Four), g5(G, Five), g6(G, Six), g7(G, Seven), g8(G, Height),
    f1(F, One), f2(F, Two), f3(F, Three), f4(F, Four), f5(F, Five), f6(F, Six), f7(F, Seven), f8(F, Height),
    e1(E, One), e2(E, Two), e3(E, Three), e4(E, Four), e5(E, Five), e6(E, Six), e7(E, Seven), e8(E, Height),
    d1(D, One), d2(D, Two), d3(D, Three), d4(D, Four), d5(D, Five), d6(D, Six), d7(D, Seven), d8(D, Height),
    c1(C, One), c2(C, Two), c3(C, Three), c4(C, Four), c5(C, Five), c6(C, Six), c7(C, Seven), c8(C, Height),
    b1(B, One), b2(B, Two), b3(B, Three), b4(B, Four), b5(B, Five), b6(B, Six), b7(B, Seven), b8(B, Height),
    a1(A, One), a2(A, Two), a3(A, Three), a4(A, Four), a5(A, Five), a6(A, Six), a7(A, Seven), a8(A, Height);

    private final File file;
    private final Row row;

    Position(File file, Row row) {
        this.file = file;
        this.row = row;
    }

    public static Set<Position> generateAllPositions() {
        return Arrays.stream(Position.values()).collect(Collectors.toSet());
    }

    public static Position posToSquare(File file, Row row) {
        return valueOf(file.getFileName() + row.getRowName());
    }

    public static Position posToSquare(int x, int y) {
        return posToSquare(File.fromNumber(x), Row.fromNumber(y));
    }

    public boolean isBorder() {
        return getFile() == A || getFile() == H || getRow() == One || getRow() == Height;
    }

    public boolean areNearby(Position position) {
        return (getFile() == position.getFile() && areNearbyRow(position))
                || (getRow() == position.getRow() && areNearbyFile(position));
    }

    private boolean areNearbyFile(Position position) {
        return getFile().distanceTo(position.getFile()) == 1;
    }

    private boolean areNearbyRow(Position position) {
        return getRow().distanceTo(position.getRow()) == 1;
    }

    public boolean hasNoPositionBetween(Position position) {
        return getRow().distanceTo(position.getRow()) <= 1 &&
                getFile().distanceTo(position.getFile()) <= 1;
    }
}
