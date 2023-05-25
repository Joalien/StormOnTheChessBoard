package fr.kubys.api;

import fr.kubys.card.Card;
import fr.kubys.core.Position;

import java.util.List;

public interface ChessBoardWriteService {
    boolean startGame();

    boolean tryToMove(Position from, Position to);

    boolean tryToPlayCard(Card card, List<?> params);

    boolean tryToPass();
}
