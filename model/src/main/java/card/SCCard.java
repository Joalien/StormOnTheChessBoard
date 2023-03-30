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

    public final boolean play(ChessBoard chessBoard) {
        validInput(chessBoard);

        doesNotCreateCheck(chessBoard);

        return doAction(chessBoard);
    }

    protected abstract void validInput(ChessBoard chessBoard);

    protected abstract void doesNotCreateCheck(ChessBoard chessBoard) throws CheckException;

    protected abstract boolean doAction(ChessBoard chessBoard);


    @Override
    public String toString() {
        return this.name;
    }
}
