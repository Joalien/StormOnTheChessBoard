package piece;

import java.util.Map;

public interface Castlable {

    static final Map<String, String> CASTLE_MAP = Map.of("f1", "h1",
            "d1", "a1",
            "f8", "h8",
            "d8", "a8");

    boolean canCastle();

    void cannotCastleAnymore();

}
