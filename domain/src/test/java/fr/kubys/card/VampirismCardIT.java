package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class VampirismCardIT {

    ChessBoard board;
    VampirismCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        board.setTurn(Color.WHITE);
        card = new VampirismCard();
    }

    @Test
    void pawn_capturing_knight_becomes_knight() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d4);
        board.add(new Knight(Color.BLACK), e5);

        board.move(pawn, e5); // pawn captures knight

        card.playOn(board, new PieceCardParam(pawn));

        Piece transformed = board.at(e5).getPiece().orElseThrow();
        assertInstanceOf(Knight.class, transformed);
        assertEquals(Color.WHITE, transformed.getColor());
        assertFalse(transformed.isKing(), "Non-king piece should not become checkmate target");
    }

    @Test
    void rook_capturing_bishop_becomes_bishop() {
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, d1);
        board.add(new Bishop(Color.BLACK), d5);

        board.move(rook, d5);

        card.playOn(board, new PieceCardParam(rook));

        Piece transformed = board.at(d5).getPiece().orElseThrow();
        assertInstanceOf(Bishop.class, transformed);
        assertEquals(Color.WHITE, transformed.getColor());
    }

    @Test
    void queen_capturing_rook_becomes_rook() {
        Queen queen = new Queen(Color.WHITE);
        board.add(queen, b2);
        board.add(new Rock(Color.BLACK), b7);

        board.move(queen, b7);

        card.playOn(board, new PieceCardParam(queen));

        Piece transformed = board.at(b7).getPiece().orElseThrow();
        assertInstanceOf(Rock.class, transformed);
        assertEquals(Color.WHITE, transformed.getColor());
    }

    @Test
    void knight_capturing_pawn_becomes_pawn() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        board.add(new Pawn(Color.BLACK), e6);

        board.move(knight, e6);

        card.playOn(board, new PieceCardParam(knight));

        Piece transformed = board.at(e6).getPiece().orElseThrow();
        assertInstanceOf(Pawn.class, transformed);
        assertEquals(Color.WHITE, transformed.getColor());
    }

    @Test
    void should_fail_if_no_capture_this_turn() {
        Rock rook = new Rock(Color.WHITE);
        board.add(rook, d1);

        board.move(rook, d5); // move without capture

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(rook)));
    }

    @Test
    void should_fail_if_piece_is_same_type_as_captured() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, d4);
        board.add(new Knight(Color.BLACK), e6);

        board.move(knight, e6);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }

    @Test
    void should_fail_for_opponent_piece() {
        Knight knight = new Knight(Color.BLACK);
        board.add(knight, d4);

        assertThrows(CannotMoveThisColorException.class,
                () -> card.playOn(board, new PieceCardParam(knight)));
    }

    @Test
    void king_capturing_queen_becomes_queen_and_remains_checkmate_target() {
        board.add(new Queen(Color.BLACK), b2);
        King king = (King) board.at(a1).getPiece().orElseThrow();
        assertTrue(king.isKing());

        board.move(king, b2);

        card.playOn(board, new PieceCardParam(king));

        Piece transformed = board.at(b2).getPiece().orElseThrow();
        assertInstanceOf(Queen.class, transformed);
        assertEquals(Color.WHITE, transformed.getColor());
        assertTrue(transformed.isKing(), "Transformed piece should still be the checkmate target");
    }

    @Test
    void king_capturing_knight_becomes_knight_and_remains_checkmate_target() {
        board.add(new Knight(Color.BLACK), b2);
        King king = (King) board.at(a1).getPiece().orElseThrow();

        board.move(king, b2);

        card.playOn(board, new PieceCardParam(king));

        Piece transformed = board.at(b2).getPiece().orElseThrow();
        assertInstanceOf(Knight.class, transformed);
        assertEquals(Color.WHITE, transformed.getColor());
        assertTrue(transformed.isKing(), "Transformed piece should still be the checkmate target");
    }

    @Test
    void king_capturing_pawn_becomes_pawn() {
        board.add(new Pawn(Color.BLACK), a2);
        King king = (King) board.at(a1).getPiece().orElseThrow();

        board.move(king, a2);

        card.playOn(board, new PieceCardParam(king));

        Piece transformed = board.at(a2).getPiece().orElseThrow();
        assertInstanceOf(Pawn.class, transformed);
        assertEquals(Color.WHITE, transformed.getColor());
    }

    @Test
    void king_vampirised_into_pawn_can_promote_next_turn() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        board.add(new Pawn(Color.BLACK), b7);
        board.setTurn(Color.WHITE);

        // Turn 1: King captures pawn on b7
        King king = (King) board.at(a1).getPiece().orElseThrow();
        board.move(king, b7); // illegal in real chess (too far), but move() doesn't validate distance

        // Vampirism: King becomes a Pawn on b7
        card.playOn(board, new PieceCardParam(king));
        Piece pawnOnB7 = board.at(b7).getPiece().orElseThrow();
        assertInstanceOf(Pawn.class, pawnOnB7);

        // Turn 2: the pawn advances to b8 and auto-promotes to Queen
        board.move(pawnOnB7, b8);

        Piece promoted = board.at(b8).getPiece().orElseThrow();
        assertInstanceOf(Queen.class, promoted);
        assertEquals(Color.WHITE, promoted.getColor());
    }

    @Test
    void black_player_can_use_vampirism() {
        board.setTurn(Color.BLACK);
        Knight blackKnight = new Knight(Color.BLACK);
        board.add(blackKnight, e5);
        board.add(new Queen(Color.WHITE), d3);

        board.move(blackKnight, d3);

        card.playOn(board, new PieceCardParam(blackKnight));

        Piece transformed = board.at(d3).getPiece().orElseThrow();
        assertInstanceOf(Queen.class, transformed);
        assertEquals(Color.BLACK, transformed.getColor());
    }
}
