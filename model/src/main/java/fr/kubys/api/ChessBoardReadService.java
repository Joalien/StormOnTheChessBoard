package fr.kubys.api;

import fr.kubys.board.effect.Effect;
import fr.kubys.card.Card;
import fr.kubys.piece.Piece;
import fr.kubys.player.Player;

import java.util.List;
import java.util.Set;

public interface ChessBoardReadService {
    Set<Piece> getPieces();

    Set<Effect> getEffects();

    List<Card> getCards();

    Player getWhite();

    Player getBlack();
}
