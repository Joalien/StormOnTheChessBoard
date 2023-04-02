package card;

import board.CheckException;
import board.ChessBoard;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import piece.Color;

@Getter
@Slf4j
public abstract class SCCard {

    protected final String name;
    protected final String description;
    protected final SCType type;
    @Setter
    protected Color isPlayedBy;

    public SCCard(String name, String description, SCType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public final boolean playOn(ChessBoard chessBoard) {
        validInput(chessBoard);

        if (!doesNotCreateCheck(chessBoard)) throw new CheckException();
        log.info("{} card is played!", this.name);
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
