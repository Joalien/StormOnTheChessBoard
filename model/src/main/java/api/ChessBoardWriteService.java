package api;

import card.Card;
import effet.Effect;
import piece.Piece;
import player.Player;

import java.util.List;
import java.util.Set;

public interface ChessBoardWriteService {
    boolean startGame();

    boolean tryToMove(String from, String to);

    boolean tryToPlayCard(Card card, List<?> params);

    boolean tryToPass();
}
