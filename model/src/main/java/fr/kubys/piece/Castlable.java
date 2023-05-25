package fr.kubys.piece;

import fr.kubys.core.Position;

import java.util.Map;

public interface Castlable {

    static final Map<Position, Position> CASTLE_MAP = Map.of(Position.f1, Position.h1,
            Position.d1, Position.a1,
            Position.f8, Position.h8,
            Position.d8, Position.a8);

    boolean canCastle();

    void cannotCastleAnymore();

}
