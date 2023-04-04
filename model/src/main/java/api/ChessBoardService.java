package api;

import card.Card;

import java.util.List;

public interface ChessBoardService {
    void startGame();

    boolean tryToMove(String from, String to);

    boolean tryToPlayCard(Card card, List<?> params);

    boolean tryToPass();
}
