package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BarricadeEffect;
import fr.kubys.board.effect.BlackHoleEffect;
import fr.kubys.board.effect.BombingEffect;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NuclearBombCardIT {

    ChessBoard board;
    NuclearBombCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        board.setTurn(Color.WHITE);
        card = new NuclearBombCard();
    }

    @Test
    void explosion_does_not_trigger_bombing_effect() {
        // BombingEffect on e5, set by black. White knight on d4, white bishop on e5.
        // Bishop is already on e5 before bomb is placed. Exploding knight removes bishop —
        // but since it's a removal (not a move), the bomb should NOT trigger.
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, e5);
        board.addEffect(new BombingEffect(e5, Color.BLACK));

        // Explode knight on d4 — bishop on d5 is adjacent and gets removed
        card.playOn(board, new PieceCardParam(knight));

        assertTrue(board.getOutOfTheBoardPieces().contains(knight), "knight should be removed");
        assertTrue(board.getOutOfTheBoardPieces().contains(bishop), "bishop should be removed");
        // BombingEffect should still be active (removal is not a move)
        assertTrue(board.getEffects().stream().anyMatch(e -> e instanceof BombingEffect), "bombing should still exist");
    }

    @Test
    void explosion_ignores_barricade() {
        // Barricade between d4|e4 and d5|e5 — normally blocks movement
        board.addEffect(new BarricadeEffect(d4, e4, d5, e5));
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, e4); // on the other side of the barricade

        // Explosion ignores barricade — it's not a movement
        card.playOn(board, new PieceCardParam(knight));

        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemyBishop));
    }

    @Test
    void explosion_ignores_black_hole() {
        // Black hole on e4 — normally blocks movement to/through it
        board.addEffect(new BlackHoleEffect(e4));
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        Bishop enemyBishop = new Bishop(Color.BLACK);
        board.add(enemyBishop, d5); // adjacent to knight, not on black hole

        // Explosion removes adjacent pieces regardless of nearby black holes
        card.playOn(board, new PieceCardParam(knight));

        assertTrue(board.getOutOfTheBoardPieces().contains(knight));
        assertTrue(board.getOutOfTheBoardPieces().contains(enemyBishop));
    }
}
