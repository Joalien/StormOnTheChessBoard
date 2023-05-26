package fr.kubys.core;



import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.kubys.core.File.*;

public enum Position {
    h1(H, Row.One), h2(H, Row.Two), h3(H, Row.Three), h4(H, Row.Four), h5(H, Row.Five), h6(H, Row.Six), h7(H, Row.Seven), h8(H, Row.Height),
    g1(G, Row.One), g2(G, Row.Two), g3(G, Row.Three), g4(G, Row.Four), g5(G, Row.Five), g6(G, Row.Six), g7(G, Row.Seven), g8(G, Row.Height),
    f1(F, Row.One), f2(F, Row.Two), f3(F, Row.Three), f4(F, Row.Four), f5(F, Row.Five), f6(F, Row.Six), f7(F, Row.Seven), f8(F, Row.Height),
    e1(E, Row.One), e2(E, Row.Two), e3(E, Row.Three), e4(E, Row.Four), e5(E, Row.Five), e6(E, Row.Six), e7(E, Row.Seven), e8(E, Row.Height),
    d1(D, Row.One), d2(D, Row.Two), d3(D, Row.Three), d4(D, Row.Four), d5(D, Row.Five), d6(D, Row.Six), d7(D, Row.Seven), d8(D, Row.Height),
    c1(C, Row.One), c2(C, Row.Two), c3(C, Row.Three), c4(C, Row.Four), c5(C, Row.Five), c6(C, Row.Six), c7(C, Row.Seven), c8(C, Row.Height),
    b1(B, Row.One), b2(B, Row.Two), b3(B, Row.Three), b4(B, Row.Four), b5(B, Row.Five), b6(B, Row.Six), b7(B, Row.Seven), b8(B, Row.Height),
    a1(A, Row.One), a2(A, Row.Two), a3(A, Row.Three), a4(A, Row.Four), a5(A, Row.Five), a6(A, Row.Six), a7(A, Row.Seven), a8(A, Row.Height);

    private final File file;
    private final Row row;

    Position(File file, Row row) {
        this.file = file;
        this.row = row;
    }

    public static Set<Position> generateAllPositions() {
        return Arrays.stream(Position.values()).collect(Collectors.toSet());
    }

    public static Position posToSquare(int x, int y) {
        return posToSquare(File.fromNumber(x), Row.fromNumber(y));
    }

    public static Position posToSquare(File file, Row row) {
        return valueOf(file.getFileName() + row.getRowName());
    }

    public boolean isBorder() {
        return getFile() == A || getFile() == H || getRow() == Row.One || getRow() == Row.Height;
    }

    public boolean areNearby(Position position) {
        return (getFile() == position.getFile() && areNearbyRow(position))
                || (getRow() == position.getRow() && areNearbyFile(position));
    }

    private boolean areNearbyRow(Position position) {
        return getRow().distanceTo(position.getRow()) == 1;
    }

    private boolean areNearbyFile(Position position) {
        return getFile().distanceTo(position.getFile()) == 1;
    }

    public boolean hasNoPositionBetween(Position position) {
        return getRow().distanceTo(position.getRow()) <= 1 &&
                getFile().distanceTo(position.getFile()) <= 1;
    }

    @Override
    public String toString() {
        return this.name();
    }

    public File getFile() {
        return this.file;
    }

    public Row getRow() {
        return this.row;
    }
}
