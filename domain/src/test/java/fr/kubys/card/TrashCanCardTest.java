package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BarricadeEffect;
import fr.kubys.board.effect.BlackHoleEffect;
import fr.kubys.board.effect.ManHoleEffect;
import fr.kubys.card.params.EffectCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class TrashCanCardTest {

    ChessBoard board;
    TrashCanCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new TrashCanCard();
    }

    @Test
    void should_remove_effect() {
        BlackHoleEffect blackHole = new BlackHoleEffect(d4);
        board.addEffect(blackHole);

        card.playOn(board, new EffectCardParam(blackHole));

        assertFalse(board.getEffects().contains(blackHole));
    }

    @Test
    void should_remove_manhole_effect() {
        ManHoleEffect manhole = new ManHoleEffect(c3, f6);
        board.addEffect(manhole);

        card.playOn(board, new EffectCardParam(manhole));

        assertFalse(board.getEffects().contains(manhole));
    }

    @Test
    void should_reject_when_effect_not_active() {
        BlackHoleEffect notOnBoard = new BlackHoleEffect(d4);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new EffectCardParam(notOnBoard)));
    }

    @Test
    void should_remove_only_one_effect() {
        BlackHoleEffect hole1 = new BlackHoleEffect(d4);
        BlackHoleEffect hole2 = new BlackHoleEffect(f5);
        board.addEffect(hole1);
        board.addEffect(hole2);

        card.playOn(board, new EffectCardParam(hole1));

        assertFalse(board.getEffects().contains(hole1));
        assertTrue(board.getEffects().contains(hole2));
    }

    @Test
    void should_reject_when_removing_effect_creates_check() {
        // Barricade between e3-e4 and f3-f4 blocks enemy rook on e5 from checking king on e1
        Rock enemyRook = new Rock(Color.BLACK);
        board.add(enemyRook, e5);
        BarricadeEffect barricade = new BarricadeEffect(e3, e4, f3, f4);
        board.addEffect(barricade);

        assertDoesNotThrow(
                () -> card.playOn(board, new EffectCardParam(barricade)));
    }
}
