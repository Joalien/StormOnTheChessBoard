package fr.kubys.board.effect;

import fr.kubys.core.Position;

public class BlackHoleEffect extends Effect {

    private final Position position;

    public BlackHoleEffect(Position position) {
        super("Trou noir");
        this.position = position;
        this.getPositions().add(position);
    }

    @Override
    public boolean blocksPosition(Position position) {
        return this.position.equals(position);
    }
}
