package card;

import board.CheckException;
import board.ChessBoard;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import piece.Color;

import java.util.List;

@Getter
@Slf4j
public abstract class Card {

    protected final String name;
    protected final String description;
    protected final CardType type;
    @Setter
    protected Color isPlayedBy;

    protected Card(String name, String description, CardType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public final boolean playOn(ChessBoard chessBoard, List<?> params) {
        setupParams(params);
        validInput(chessBoard);

        if (!doesNotCreateCheck(chessBoard)) throw new CheckException();
        log.info("{} card is played!", this.name);
        return doAction(chessBoard);
    }

    protected abstract void setupParams(List<?> params);

    protected abstract void validInput(ChessBoard chessBoard);

    protected abstract boolean doesNotCreateCheck(ChessBoard chessBoard);

    protected abstract boolean doAction(ChessBoard chessBoard);


    @Override
    public String toString() {
        return this.name;
    }
}
