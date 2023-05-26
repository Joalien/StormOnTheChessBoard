package fr.kubys.board.effect;

import fr.kubys.core.Position;
import fr.kubys.piece.Piece;
//import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ManHoleEffect extends Effect {
//    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ManHoleEffect.class);
    private final Map<Position, Position> holes = new HashMap<>();

    public ManHoleEffect(Position position1, Position position2) {
        super("Bouche d'égout");
        this.holes.put(position1, position2);
        this.holes.put(position2, position1);
    }

    @Override
    public boolean allowToMove(Piece piece, Position positionToMoveOn) {
        return Objects.equals(holes.get(piece.getPosition()), positionToMoveOn);
    }
}
