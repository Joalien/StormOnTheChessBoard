package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class YouTooBrutusCardTest {

    ChessBoard board;
    YouTooBrutusCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        card = new YouTooBrutusCard();
    }

    @Test
    void should_change_checking_piece_color() {
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        Rock blackRook = new Rock(Color.BLACK);
        board.add(blackRook, e5);
        // It's BLACK's turn (enemy), WHITE is the card player
        board.setTurn(Color.BLACK);

        assertTrue(board.isKingUnderAttack(Color.WHITE));
        card.playOn(board, new PieceCardParam(blackRook));

        assertEquals(Color.WHITE, blackRook.getColor());
    }

    @Test
    void should_reject_when_king_not_in_check() {
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        Rock blackRook = new Rock(Color.BLACK);
        board.add(blackRook, a8);
        board.setTurn(Color.BLACK);

        assertThrows(IllegalStateException.class,
                () -> card.playOn(board, new PieceCardParam(blackRook)));
    }

    @Test
    void should_change_knight_checking_piece_color() {
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        Knight blackKnight = new Knight(Color.BLACK);
        board.add(blackKnight, d3);
        board.setTurn(Color.BLACK);

        assertTrue(board.isKingUnderAttack(Color.WHITE));
        card.playOn(board, new PieceCardParam(blackKnight));

        assertEquals(Color.WHITE, blackKnight.getColor());
    }

    @Test
    void should_work_for_black_card_player() {
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        Queen whiteQueen = new Queen(Color.WHITE);
        board.add(whiteQueen, e5);
        // It's WHITE's turn (enemy), BLACK is the card player
        board.setTurn(Color.WHITE);

        assertTrue(board.isKingUnderAttack(Color.BLACK));
        card.playOn(board, new PieceCardParam(whiteQueen));

        assertEquals(Color.BLACK, whiteQueen.getColor());
    }

    @Test
    void should_reject_piece_not_giving_check() {
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        Rock checkingRook = new Rock(Color.BLACK);
        Rock otherRook = new Rock(Color.BLACK);
        board.add(checkingRook, e5);
        board.add(otherRook, a8);
        board.setTurn(Color.BLACK);

        assertTrue(board.isKingUnderAttack(Color.WHITE));
        // otherRook is not giving check
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(otherRook)));
    }

    @Test
    void should_select_specific_piece_when_double_check() {
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        Rock blackRook = new Rock(Color.BLACK);
        Bishop blackBishop = new Bishop(Color.BLACK);
        board.add(blackRook, e5);
        board.add(blackBishop, b4);
        board.setTurn(Color.BLACK);

        // Both pieces give check
        assertTrue(board.isKingUnderAttack(Color.WHITE));
        // Player selects the bishop
        card.playOn(board, new PieceCardParam(blackBishop));

        assertEquals(Color.WHITE, blackBishop.getColor());
        // Rook stays black
        assertEquals(Color.BLACK, blackRook.getColor());
    }

    @Test
    void should_reject_own_piece() {
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        Rock blackRook = new Rock(Color.BLACK);
        board.add(blackRook, e5);
        Knight whiteKnight = new Knight(Color.WHITE);
        board.add(whiteKnight, c2);
        board.setTurn(Color.BLACK);

        assertTrue(board.isKingUnderAttack(Color.WHITE));
        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(whiteKnight)));
    }
}
