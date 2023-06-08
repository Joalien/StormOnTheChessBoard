package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Color;

import java.util.Objects;

public abstract class Card<T extends CardParam> {

    protected final Integer id;
    protected final String name;
    protected final String description;
    protected final CardType type;
    protected final Class<T> clazz;
    private static Integer maxGameId = 1;

    protected Card(String name, String description, CardType type, Class<T> tClass) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.clazz = tClass;
        this.id = maxGameId++;
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

    public Integer getId() {
        return id;
    }
}
