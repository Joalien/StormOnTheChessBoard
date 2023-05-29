package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;

import java.util.List;
import java.util.Objects;

public abstract class Card {

    protected final String name;
    protected final String description;
    protected final CardType type;
    protected Color isPlayedBy;

    protected Card(String name, String description, CardType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public final void playOn(ChessBoard chessBoard, List<?> params) {
        setupParams(params);
        validInput(chessBoard);

        if (!doesNotCreateCheck(chessBoard)) throw new CheckException();
//        log.info("{} card is played!", this.name);
        doAction(chessBoard);
    }

    protected abstract void setupParams(List<?> params);

    protected abstract void validInput(ChessBoard chessBoard);

    protected abstract boolean doesNotCreateCheck(ChessBoard chessBoard);

    protected abstract void doAction(ChessBoard chessBoard);


    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public CardType getType() {
        return this.type;
    }

    public Color getIsPlayedBy() {
        return this.isPlayedBy;
    }

    public void setIsPlayedBy(Color isPlayedBy) {
        this.isPlayedBy = isPlayedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(name, card.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
