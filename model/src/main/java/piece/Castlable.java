package piece;

import position.Position;

import java.util.Map;

import static position.Position.*;

public interface Castlable {

    static final Map<Position, Position> CASTLE_MAP = Map.of(f1, h1,
            d1, a1,
            f8, h8,
            d8, a8);

    boolean canCastle();

    void cannotCastleAnymore();

}
