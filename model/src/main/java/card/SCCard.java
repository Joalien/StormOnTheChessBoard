package card;

import board.CheckException;
import board.ChessBoard;
import lombok.Getter;

@Getter
public abstract class SCCard {

    protected final String name;
    protected final String description;
    protected final SCType type;

    public SCCard(String name, String description, SCType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public final boolean playOn(ChessBoard chessBoard) {
        validInput(chessBoard);

        if (!doesNotCreateCheck(chessBoard)) throw new CheckException();

        return doAction(chessBoard);
    }

    protected abstract void validInput(ChessBoard chessBoard);

    protected abstract boolean doesNotCreateCheck(ChessBoard chessBoard);

    protected abstract boolean doAction(ChessBoard chessBoard);


    @Override
    public String toString() {
        return this.name;
    }
}
