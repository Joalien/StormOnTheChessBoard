package api;

import card.Card;
import core.Position;

import java.util.List;

public interface ChessBoardWriteService {
    boolean startGame();

    boolean tryToMove(Position from, Position to);

    boolean tryToPlayCard(Card card, List<?> params);

    boolean tryToPass();
}
