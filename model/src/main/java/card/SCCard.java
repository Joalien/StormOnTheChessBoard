package card;

import board.ChessBoard;
import lombok.Getter;

@Getter
public abstract class SCCard {

    protected final String name;
    protected final String description;

    protected SCCard(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract boolean play(ChessBoard chessBoard);

    @Override
    public String toString() {
        return this.name;
    }
}
