package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.TwoPieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class RaccoonCardTest {

    ChessBoard board;
    RaccoonCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new RaccoonCard();
    }

    @Test
    void should_swap_own_pawn_and_bishop() {
        WhitePawn pawn = new WhitePawn();
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(pawn, d2);
        board.add(bishop, c1);

        card.playOn(board, new TwoPieceCardParam(pawn, bishop));

        assertInstanceOf(WhitePawn.class, board.at(c1).getPiece().get());
        assertInstanceOf(Bishop.class, board.at(d2).getPiece().get());
    }

    @Test
    void should_swap_own_pawn_and_knight() {
        WhitePawn pawn = new WhitePawn();
        Knight knight = new Knight(Color.WHITE);
        board.add(pawn, d2);
        board.add(knight, b1);

        card.playOn(board, new TwoPieceCardParam(pawn, knight));

        assertInstanceOf(WhitePawn.class, board.at(b1).getPiece().get());
        assertInstanceOf(Knight.class, board.at(d2).getPiece().get());
    }

    @Test
    void should_swap_own_pawn_and_rook() {
        WhitePawn pawn = new WhitePawn();
        Rock rook = new Rock(Color.WHITE);
        board.add(pawn, d2);
        board.add(rook, a1);

        card.playOn(board, new TwoPieceCardParam(pawn, rook));

        assertInstanceOf(WhitePawn.class, board.at(a1).getPiece().get());
        assertInstanceOf(Rock.class, board.at(d2).getPiece().get());
    }

    @Test
    void should_reject_enemy_pieces() {
        BlackPawn pawn = new BlackPawn();
        Bishop bishop = new Bishop(Color.BLACK);
        board.add(pawn, d7);
        board.add(bishop, c8);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new TwoPieceCardParam(pawn, bishop)));
    }

    @Test
    void should_reject_two_pawns() {
        WhitePawn pawn1 = new WhitePawn();
        WhitePawn pawn2 = new WhitePawn();
        board.add(pawn1, d2);
        board.add(pawn2, c2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(pawn1, pawn2)));
    }

    @Test
    void should_promote_white_pawn_swapped_to_last_rank() {
        WhitePawn pawn = new WhitePawn();
        Rock rook = new Rock(Color.WHITE);
        board.add(pawn, d7);
        board.add(rook, h8);

        card.playOn(board, new TwoPieceCardParam(pawn, rook));

        // Pawn lands on h8 (last rank for white) → auto-promoted to Queen
        assertInstanceOf(Queen.class, board.at(h8).getPiece().get());
        assertEquals(Color.WHITE, board.at(h8).getPiece().get().getColor());
        // Rook moved to d7
        assertInstanceOf(Rock.class, board.at(d7).getPiece().get());
    }

    @Test
    void should_promote_black_pawn_swapped_to_last_rank() {
        BlackPawn pawn = new BlackPawn();
        Rock rook = new Rock(Color.BLACK);
        board.add(pawn, d2);
        board.add(rook, h1);
        board.setTurn(Color.BLACK);

        card.playOn(board, new TwoPieceCardParam(pawn, rook));

        // Pawn lands on h1 (last rank for black) → auto-promoted to Queen
        assertInstanceOf(Queen.class, board.at(h1).getPiece().get());
        assertEquals(Color.BLACK, board.at(h1).getPiece().get().getColor());
    }

    @Test
    void should_reject_pawn_and_queen() {
        WhitePawn pawn = new WhitePawn();
        Queen queen = new Queen(Color.WHITE);
        board.add(pawn, d2);
        board.add(queen, d1);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new TwoPieceCardParam(pawn, queen)));
    }
}
