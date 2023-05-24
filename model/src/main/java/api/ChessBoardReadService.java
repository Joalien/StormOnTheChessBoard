package api;

import card.Card;
import board.effect.Effect;
import piece.Piece;
import player.Player;

import java.util.List;
import java.util.Set;

public interface ChessBoardReadService {
    Set<Piece> getPieces();

    Set<Effect> getEffects();

    List<Card> getCards();

    Player getWhite();

    Player getBlack();
}
