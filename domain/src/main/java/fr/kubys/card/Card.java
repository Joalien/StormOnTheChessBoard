package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.CardParam;

import java.util.Objects;

public abstract class Card<T extends CardParam> {

    //FIXME add functional id
    protected final String name;
    protected final String description;
    protected final CardType type;
    protected final Class<T> clazz;
    protected final boolean persistent;

    protected Card(String name, String description, CardType type, Class<T> clazz) {
        this(name, description, type, clazz, false);
    }

    protected Card(String name, String description, CardType type, Class<T> clazz, boolean persistent) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.clazz = clazz;
        this.persistent = persistent;
    }

    public final void playOn(ChessBoard chessBoard, T param) {
        validInput(chessBoard, param);

        if (!doesNotCreateCheck(chessBoard, param)) throw new CheckException();
//        log.info("{} card is played!", this.name);
        doAction(chessBoard, param);
    }

    protected abstract void validInput(ChessBoard chessBoard, T param);

    protected abstract boolean doesNotCreateCheck(ChessBoard chessBoard, T param);

    protected abstract void doAction(ChessBoard chessBoard, T param);

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public CardType getType() {
        return this.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card<?> card = (Card<?>) o;
        return Objects.equals(name, card.name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

    public boolean isPersistent() {
        return this.persistent;
    }
}
