package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.FrenzyEffect;
import fr.kubys.board.effect.HideoutEffect;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class FrenzyCardIT {

    ChessBoard board;
    FrenzyCard frenzy;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        frenzy = new FrenzyCard();
    }

    @Test
    void should_allow_normal_move_when_only_attacker_is_asleep() {
        // White Knight on c3 is the only piece that could capture the Black Pawn on d5,
        // but it's frozen by HideoutEffect. White also owns a quiet Pawn on a2.
        Knight whiteKnight = new Knight(Color.WHITE);
        board.add(whiteKnight, c3);
        Pawn blackTargetPawn = new Pawn(Color.BLACK);
        board.add(blackTargetPawn, d5);
        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, a2);

        // Hideout puts the white knight to sleep, then Frenzy is cast.
        board.addEffect(new HideoutEffect(whiteKnight));
        frenzy.playOn(board, new NoCardParam());
        // Frenzy.afterMoveHook may have removed itself from a previous capture; re-cast
        // sanity: confirm both effects are still active for the test setup.
        assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof HideoutEffect),
                "Hideout must still be active for the test premise");
        assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof FrenzyEffect),
                "Frenzy must still be active at the start of the test");

        // Frenzy must recognise that the only attacker is asleep ⇒ no real capture is
        // available ⇒ the innocent pawn on a2 is free to advance to a3.
        assertDoesNotThrow(() -> board.tryToMove(a2, a3),
                "When the only piece capable of capturing is asleep, Frenzy must allow a normal move");

        // Sanity: the knight stayed put (still asleep) and the pawn moved.
        assertTrue(board.at(c3).getPiece().isPresent(), "Knight should still be on c3");
        assertEquals(whitePawn, board.at(a3).getPiece().orElseThrow());
        // Frenzy auto-removes itself in beforeMoveHook when no capture is possible.
        assertTrue(board.getEffects().stream().noneMatch(e -> e instanceof FrenzyEffect),
                "Frenzy should auto-remove itself once it realises no capture is reachable");
    }
}
