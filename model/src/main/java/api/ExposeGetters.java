package api;

import board.ChessBoard;
import card.Card;
import effet.Effect;
import piece.Piece;
import player.Player;
import state.StateEnum;

import java.util.List;
import java.util.Set;

public interface ExposeGetters {
    Player getWhite();

    Player getBlack();

    List<Card> getCards();

    Set<Piece> getPieces();

    Set<Effect> getEffects();
}

