package card;

import board.CheckException;
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