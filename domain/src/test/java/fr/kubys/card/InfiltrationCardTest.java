package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class InfiltrationCardTest {

    ChessBoard board;
    InfiltrationCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new InfiltrationCard();
    }

    @Test
    void should_swap_own_and_enemy_pawn() {
        Pawn own = new Pawn(Color.WHITE);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(own, a4);
        board.add(enemy, a5);

        card.playOn(board, new TwoPieceCardParam(own, enemy));

        assertEquals(Color.WHITE, board.at(a5).getPiece().get().getColor());
        assertEquals(Color.BLACK, board.at(a4).getPiece().get().getColor());
    }

    @Test
    void should_promote_white_pawn_swapped_to_last_rank() {
        // White pawn on a7, black pawn on a8 → swap → white pawn lands on a8 → promotion
        Pawn own = new Pawn(Color.WHITE);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(own, a7);
        board.add(enemy, a8);

        card.playOn(board, new TwoPieceCardParam(own, enemy));

        assertInstanceOf(Queen.class, board.at(a8).getPiece().get());
        assertEquals(Color.WHITE, board.at(a8).getPiece().get().getColor());
    }

    @Test
    void should_promote_black_pawn_swapped_to_last_rank() {
        // White pawn on h1, black pawn on h2 → swap → black pawn lands on h1 → promotion
        Pawn own = new Pawn(Color.WHITE);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(own, h1);
        board.add(enemy, h2);

        card.playOn(board, new TwoPieceCardParam(own, enemy));

        assertInstanceOf(Queen.class, board.at(h1).getPiece().get());
        assertEquals(Color.BLACK, board.at(h1).getPiece().get().getColor());
    }

    @Test
    void should_promote_both_pawns_when_both_land_on_last_rank() {
        // White pawn on a8's row? No, white promotes on row 8, black promotes on row 1
        // White pawn on h1, black pawn on h8 → swap → white on h8 (promotes), black on h1 (promotes)
        Pawn own = new Pawn(Color.WHITE);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(own, h1);
        board.add(enemy, h8);

        card.playOn(board, new TwoPieceCardParam(own, enemy));

        assertInstanceOf(Queen.class, board.at(h8).getPiece().get());
        assertEquals(Color.WHITE, board.at(h8).getPiece().get().getColor());
        assertInstanceOf(Queen.class, board.at(h1).getPiece().get());
        assertEquals(Color.BLACK, board.at(h1).getPiece().get().getColor());
    }

    @Test
    void should_reject_two_own_pawns() {
        Pawn p1 = new Pawn(Color.WHITE);
        Pawn p2 = new Pawn(Color.WHITE);
        board.add(p1, d2);
        board.add(p2, c2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(p1, p2)));
    }

    @Test
    void should_reject_two_enemy_pawns() {
        Pawn p1 = new Pawn(Color.BLACK);
        Pawn p2 = new Pawn(Color.BLACK);
        board.add(p1, d7);
        board.add(p2, c7);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(p1, p2)));
    }

    @Test
    void should_reject_non_pawns() {
        Knight knight = new Knight(Color.WHITE);
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(knight, b1);
        board.add(pawn, d7);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(knight, pawn)));
    }

    @Test
    void should_reject_non_pawn_enemy_piece() {
        Pawn pawn = new Pawn(Color.WHITE);
        Rock rook = new Rock(Color.BLACK);
        board.add(pawn, d4);
        board.add(rook, a8);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(pawn, rook)));
    }
}
