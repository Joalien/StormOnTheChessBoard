package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class BetrayalCardTest {

    ChessBoard board;
    BetrayalCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new BetrayalCard();
    }

    @Test
    void should_convert_enemy_pawn_on_white_side() {
        Pawn blackPawn = new Pawn(Color.BLACK);
        board.add(blackPawn, d3); // Row 3 = white's side

        card.playOn(board, new PieceCardParam(blackPawn));

        assertEquals(Color.WHITE, blackPawn.getColor());
    }

    @Test
    void should_convert_enemy_pawn_on_black_side() {
        board.setTurn(Color.BLACK);
        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, d6); // Row 6 = black's side

        card.playOn(board, new PieceCardParam(whitePawn));

        assertEquals(Color.BLACK, whitePawn.getColor());
    }

    @Test
    void should_reject_pawn_not_on_my_side() {
        Pawn blackPawn = new Pawn(Color.BLACK);
        board.add(blackPawn, d6); // Row 6 = black's side, not white's

        assertThrows(IllegalArgumentException.class, () -> card.playOn(board, new PieceCardParam(blackPawn)));
    }

    @Test
    void should_reject_own_pawn() {
        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, d3);

        assertThrows(IllegalArgumentException.class, () -> card.playOn(board, new PieceCardParam(whitePawn)));
    }

    @Test
    void should_reject_non_pawn() {
        Knight blackKnight = new Knight(Color.BLACK);
        board.add(blackKnight, c3);

        assertThrows(IllegalArgumentException.class, () -> card.playOn(board, new PieceCardParam(blackKnight)));
    }

    @Test
    void should_accept_pawn_on_border_row_4_as_white_side() {
        Pawn blackPawn = new Pawn(Color.BLACK);
        board.add(blackPawn, d4); // Row 4 = still white's side

        assertDoesNotThrow(() -> card.playOn(board, new PieceCardParam(blackPawn)));
        assertEquals(Color.WHITE, blackPawn.getColor());
    }
}
