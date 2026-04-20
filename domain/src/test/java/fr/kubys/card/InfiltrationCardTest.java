package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.InfiltrationCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
    void should_swap_one_pair_of_pawns() {
        Pawn own = new Pawn(Color.WHITE);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(own, a4);
        board.add(enemy, a5);

        card.playOn(board, new InfiltrationCardParam(Set.of(own, enemy)));

        assertEquals(Color.WHITE, board.at(a5).getPiece().get().getColor());
        assertEquals(Color.BLACK, board.at(a4).getPiece().get().getColor());
    }

    @Test
    void should_swap_two_pairs_of_pawns() {
        Pawn own1 = new Pawn(Color.WHITE);
        Pawn own2 = new Pawn(Color.WHITE);
        Pawn enemy1 = new Pawn(Color.BLACK);
        Pawn enemy2 = new Pawn(Color.BLACK);
        board.add(own1, a4);
        board.add(own2, b4);
        board.add(enemy1, a5);
        board.add(enemy2, b5);

        card.playOn(board, new InfiltrationCardParam(Set.of(own1, own2, enemy1, enemy2)));

        assertEquals(Color.WHITE, board.at(a5).getPiece().get().getColor());
        assertEquals(Color.WHITE, board.at(b5).getPiece().get().getColor());
        assertEquals(Color.BLACK, board.at(a4).getPiece().get().getColor());
        assertEquals(Color.BLACK, board.at(b4).getPiece().get().getColor());
    }

    @Test
    void should_swap_three_pairs_of_pawns() {
        Pawn own1 = new Pawn(Color.WHITE);
        Pawn own2 = new Pawn(Color.WHITE);
        Pawn own3 = new Pawn(Color.WHITE);
        Pawn enemy1 = new Pawn(Color.BLACK);
        Pawn enemy2 = new Pawn(Color.BLACK);
        Pawn enemy3 = new Pawn(Color.BLACK);
        board.add(own1, a4);
        board.add(own2, b4);
        board.add(own3, c4);
        board.add(enemy1, a5);
        board.add(enemy2, b5);
        board.add(enemy3, c5);

        card.playOn(board, new InfiltrationCardParam(Set.of(own1, own2, own3, enemy1, enemy2, enemy3)));

        assertEquals(Color.WHITE, board.at(a5).getPiece().get().getColor());
        assertEquals(Color.WHITE, board.at(b5).getPiece().get().getColor());
        assertEquals(Color.WHITE, board.at(c5).getPiece().get().getColor());
        assertEquals(Color.BLACK, board.at(a4).getPiece().get().getColor());
        assertEquals(Color.BLACK, board.at(b4).getPiece().get().getColor());
        assertEquals(Color.BLACK, board.at(c4).getPiece().get().getColor());
    }

    @Test
    void should_reject_more_than_three_pairs() {
        Pawn own1 = new Pawn(Color.WHITE);
        Pawn own2 = new Pawn(Color.WHITE);
        Pawn own3 = new Pawn(Color.WHITE);
        Pawn own4 = new Pawn(Color.WHITE);
        Pawn enemy1 = new Pawn(Color.BLACK);
        Pawn enemy2 = new Pawn(Color.BLACK);
        Pawn enemy3 = new Pawn(Color.BLACK);
        Pawn enemy4 = new Pawn(Color.BLACK);
        board.add(own1, a4);
        board.add(own2, b4);
        board.add(own3, c4);
        board.add(own4, d4);
        board.add(enemy1, a5);
        board.add(enemy2, b5);
        board.add(enemy3, c5);
        board.add(enemy4, d5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new InfiltrationCardParam(Set.of(own1, own2, own3, own4, enemy1, enemy2, enemy3, enemy4))));
    }

    @Test
    void should_reject_mismatched_counts() {
        Pawn own1 = new Pawn(Color.WHITE);
        Pawn own2 = new Pawn(Color.WHITE);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(own1, a4);
        board.add(own2, b4);
        board.add(enemy, a5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new InfiltrationCardParam(Set.of(own1, own2, enemy))));
    }

    @Test
    void should_promote_white_pawn_swapped_to_last_rank() {
        Pawn own = new Pawn(Color.WHITE);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(own, a7);
        board.add(enemy, a8);

        card.playOn(board, new InfiltrationCardParam(Set.of(own, enemy)));

        assertInstanceOf(Queen.class, board.at(a8).getPiece().get());
        assertEquals(Color.WHITE, board.at(a8).getPiece().get().getColor());
    }

    @Test
    void should_promote_black_pawn_swapped_to_last_rank() {
        Pawn own = new Pawn(Color.WHITE);
        Pawn enemy = new Pawn(Color.BLACK);
        board.add(own, h1);
        board.add(enemy, h2);

        card.playOn(board, new InfiltrationCardParam(Set.of(own, enemy)));

        assertInstanceOf(Queen.class, board.at(h1).getPiece().get());
        assertEquals(Color.BLACK, board.at(h1).getPiece().get().getColor());
    }

    @Test
    void should_reject_all_same_color() {
        Pawn p1 = new Pawn(Color.WHITE);
        Pawn p2 = new Pawn(Color.WHITE);
        board.add(p1, d2);
        board.add(p2, c2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new InfiltrationCardParam(Set.of(p1, p2))));
    }

    @Test
    void should_reject_non_pawns() {
        Knight knight = new Knight(Color.WHITE);
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(knight, b1);
        board.add(pawn, d7);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new InfiltrationCardParam(Set.of(knight, pawn))));
    }
}
