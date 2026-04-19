package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BombingEffect;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class PasDeDeuxCardTest {

    ChessBoard board;
    PasDeDeuxCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new PasDeDeuxCard();
    }

    @Test
    void should_replicate_move_delta() {
        // Last move: e2 -> e4 (delta: 0, +2)
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, e2);
        board.move(pawn, e4);

        // Now apply same delta (0, +2) to a rook on a1 -> a3
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, a1);

        card.playOn(board, new PieceCardParam(rook));

        assertEquals(rook, board.at(a3).getPiece().get());
        assertTrue(board.at(a1).getPiece().isEmpty());
    }

    @Test
    void should_allow_capture_with_replicated_move() {
        // Last move: d2 -> d4 (delta: 0, +2)
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d2);
        board.move(pawn, d4);

        Rock rook = new Rock(Color.WHITE);
        board.add(rook, a1);
        Knight enemy = new Knight(Color.BLACK);
        board.add(enemy, a3);

        card.playOn(board, new PieceCardParam(rook));

        assertEquals(rook, board.at(a3).getPiece().get());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }

    @Test
    void should_reject_when_destination_out_of_bounds() {
        // Last move: e2 -> e4 (delta: 0, +2)
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, e2);
        board.move(pawn, e4);

        // Rook on a7 would go to a9 which is out of bounds
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, a7);

        assertThrows(Exception.class,
                () -> card.playOn(board, new PieceCardParam(rook)));
    }

    @Test
    void should_reject_enemy_piece() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, e2);
        board.move(pawn, e4);

        Rock enemyRook = new Rock(Color.BLACK);
        board.add(enemyRook, a1);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new PieceCardParam(enemyRook)));
    }

    @Test
    void should_reject_capture_own_piece() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, e2);
        board.move(pawn, e4);

        Rock rook = new Rock(Color.WHITE);
        board.add(rook, a1);
        Knight ownKnight = new Knight(Color.WHITE);
        board.add(ownKnight, a3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(rook)));
    }

    @Test
    void should_reject_if_creates_check() {
        board.removePieceFromTheBoard(board.at(e1).getPiece().get());
        King king = new King(Color.WHITE);
        board.add(king, d1);
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, c1);
        Queen blackQueen = new Queen(Color.BLACK);
        board.add(blackQueen, a1);

        // Last move: e2 -> e4 (delta: 0, +2)
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, e2);
        board.move(pawn, e4);

        // Rook c1 -> c3 (delta 0, +2) would expose king to queen
        assertThrows(Exception.class,
                () -> card.playOn(board, new PieceCardParam(rook)));
    }

    @Test
    void should_reject_king_move() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, e2);
        board.move(pawn, e3);

        King king = board.at(e1).getPiece().map(p -> (King) p).get();

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(king)));
    }

    @Test
    void should_trigger_bombing_effect_on_move() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, e2);
        board.move(pawn, e4);

        Rock rook = new Rock(Color.WHITE);
        board.add(rook, a1);
        board.addEffect(new BombingEffect(a3, Color.BLACK));

        card.playOn(board, new PieceCardParam(rook));

        // Rook moved to bombed square and got destroyed
        assertTrue(board.at(a3).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(rook));
    }
}
