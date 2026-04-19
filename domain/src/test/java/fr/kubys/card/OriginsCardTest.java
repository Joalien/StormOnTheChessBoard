package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.OriginsEffect;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class OriginsCardTest {

    ChessBoard board;
    OriginsCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new OriginsCard();
    }

    @Test
    void should_add_origins_effect() {
        card.playOn(board, new NoCardParam());
        assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof OriginsEffect));
    }

    @Test
    void should_remove_effect_when_king_is_in_check() {
        card.playOn(board, new NoCardParam());

        // Move a rook to put king in check
        Rock whiteRock = new Rock(Color.WHITE);
        board.add(whiteRock, e4);
        board.move(whiteRock, e7); // threatens black king on e8

        // Effect should be removed because black king is in check
        assertFalse(board.getEffects().stream().anyMatch(e -> e instanceof OriginsEffect));
    }
}
