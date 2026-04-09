package fr.kubys.api;

import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.PromotionPiece;

public interface ChessBoardWriteService {
    void startGame(long seed);

    void tryToMove(Position from, Position to);

    <T extends CardParam> void tryToPlayCard(Card<T> card, T params);

    void tryToPass();

    void promote(Position position, PromotionPiece piece);
}
