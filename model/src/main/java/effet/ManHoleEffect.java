package effet;

import lombok.extern.slf4j.Slf4j;
import piece.Piece;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class ManHoleEffect extends Effect {
    private final Map<String, String> holes = new HashMap<>();

    public ManHoleEffect(String position1, String position2) {
        super("Bouche d'égout");
        this.holes.put(position1, position2);
        this.holes.put(position2, position1);
    }

    @Override
    public boolean allowToMove(Piece piece, String positionToMoveOn) {
        return Objects.equals(holes.get(piece.getPosition()), positionToMoveOn);
    }
}
