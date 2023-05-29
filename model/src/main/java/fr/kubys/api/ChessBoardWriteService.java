package fr.kubys.api;

import fr.kubys.card.Card;
import fr.kubys.core.Position;

import java.util.List;

public interface ChessBoardWriteService {
    void startGame(long seed);

    void tryToMove(Position from, Position to);

    void tryToPlayCard(Card card, List<?> params);

    void tryToPass();
}
