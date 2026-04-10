package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class MercyCardIT {

    public static final NoCardParam NO_CARD_PARAM = new NoCardParam();
    ChessBoard board;
    MercyCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        board.setTurn(Color.WHITE);
        card = new MercyCard();
    }

    @Nested
    class RookAttacks {
        @Test
        void rook_attacks_pawn_from_behind_on_file() {
            Rock whiteRook = new Rock(Color.WHITE);
            BlackPawn blackPawn = new BlackPawn();
            board.add(whiteRook, e1);
            board.add(blackPawn, e5);

            board.move(whiteRook, e5); // rook captures pawn from below

            card.playOn(board, NO_CARD_PARAM);

            // Pawn is restored, rook stops one square before pawn
            Piece restored = board.at(e5).getPiece().orElseThrow();
            assertSame(blackPawn, restored);
            Piece stopped = board.at(e4).getPiece().orElseThrow();
            assertSame(whiteRook, stopped);
        }

        @Test
        void rook_attacks_pawn_from_front_on_file() {
            Rock whiteRook = new Rock(Color.WHITE);
            BlackPawn blackPawn = new BlackPawn();
            board.add(whiteRook, e8);
            board.add(blackPawn, e5);

            // Need to move king out of the way first
            board.removePieceFromTheBoard(board.at(h8).getPiece().get());
            board.add(new King(Color.BLACK), h7);

            board.move(whiteRook, e5); // rook captures pawn from above

            card.playOn(board, NO_CARD_PARAM);

            Piece restored = board.at(e5).getPiece().orElseThrow();
            assertSame(blackPawn, restored);
            Piece stopped = board.at(e6).getPiece().orElseThrow();
            assertSame(whiteRook, stopped);
        }

        @Test
        void rook_attacks_pawn_on_row() {
            Rock whiteRook = new Rock(Color.WHITE);
            BlackPawn blackPawn = new BlackPawn();
            board.add(whiteRook, a5);
            board.add(blackPawn, e5);

            board.move(whiteRook, e5);

            card.playOn(board, NO_CARD_PARAM);

            Piece restored = board.at(e5).getPiece().orElseThrow();
            assertSame(blackPawn, restored);
            Piece stopped = board.at(d5).getPiece().orElseThrow();
            assertSame(whiteRook, stopped);
        }

        @Test
        void rook_attacks_pawn_from_adjacent_square() {
            Rock whiteRook = new Rock(Color.WHITE);
            BlackPawn blackPawn = new BlackPawn();
            board.add(whiteRook, e4);
            board.add(blackPawn, e5);

            board.move(whiteRook, e5);

            card.playOn(board, NO_CARD_PARAM);

            Piece restored = board.at(e5).getPiece().orElseThrow();
            assertSame(blackPawn, restored);
            // Rook goes back to its original square
            Piece stopped = board.at(e4).getPiece().orElseThrow();
            assertSame(whiteRook, stopped);
        }
    }

    @Nested
    class BishopAttacks {
        @Test
        void bishop_attacks_pawn_on_diagonal() {
            Bishop whiteBishop = new Bishop(Color.WHITE);
            BlackPawn blackPawn = new BlackPawn();
            board.add(whiteBishop, b2);
            board.add(blackPawn, e5);

            board.move(whiteBishop, e5);

            card.playOn(board, NO_CARD_PARAM);

            Piece restored = board.at(e5).getPiece().orElseThrow();
            assertSame(blackPawn, restored);
            Piece stopped = board.at(d4).getPiece().orElseThrow();
            assertSame(whiteBishop, stopped);
        }
    }

    @Nested
    class QueenAttacks {
        @Test
        void queen_attacks_pawn_on_file() {
            Queen whiteQueen = new Queen(Color.WHITE);
            BlackPawn blackPawn = new BlackPawn();
            board.add(whiteQueen, d1);
            board.add(blackPawn, d6);

            board.move(whiteQueen, d6);

            card.playOn(board, NO_CARD_PARAM);

            Piece restored = board.at(d6).getPiece().orElseThrow();
            assertSame(blackPawn, restored);
            Piece stopped = board.at(d5).getPiece().orElseThrow();
            assertSame(whiteQueen, stopped);
        }

        @Test
        void queen_attacks_pawn_on_diagonal() {
            Queen whiteQueen = new Queen(Color.WHITE);
            BlackPawn blackPawn = new BlackPawn();
            board.add(whiteQueen, a2);
            board.add(blackPawn, d5);

            board.move(whiteQueen, d5);

            card.playOn(board, NO_CARD_PARAM);

            Piece restored = board.at(d5).getPiece().orElseThrow();
            assertSame(blackPawn, restored);
            Piece stopped = board.at(c4).getPiece().orElseThrow();
            assertSame(whiteQueen, stopped);
        }
    }

    @Nested
    class BlackAttacksWhite {
        @Test
        void black_rook_attacks_white_pawn_and_white_uses_mercy() {
            board.setTurn(Color.BLACK);
            Rock blackRook = new Rock(Color.BLACK);
            WhitePawn whitePawn = new WhitePawn();
            board.add(blackRook, e8);
            board.add(whitePawn, e3);

            board.move(blackRook, e3);

            card.playOn(board, NO_CARD_PARAM);

            Piece restored = board.at(e3).getPiece().orElseThrow();
            assertSame(whitePawn, restored);
            Piece stopped = board.at(e4).getPiece().orElseThrow();
            assertSame(blackRook, stopped);
        }
    }

    @Nested
    class Failure {
        @Test
        void should_fail_if_no_capture_this_turn() {
            Rock whiteRook = new Rock(Color.WHITE);
            board.add(whiteRook, d1);

            board.move(whiteRook, d5);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, NO_CARD_PARAM));
        }

        @Test
        void should_fail_if_captured_piece_is_not_a_pawn() {
            Rock whiteRook = new Rock(Color.WHITE);
            Bishop blackBishop = new Bishop(Color.BLACK);
            board.add(whiteRook, d1);
            board.add(blackBishop, d5);

            board.move(whiteRook, d5);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, NO_CARD_PARAM));
        }

        @Test
        void should_fail_if_attacker_is_a_knight() {
            Knight whiteKnight = new Knight(Color.WHITE);
            BlackPawn blackPawn = new BlackPawn();
            board.add(whiteKnight, d4);
            board.add(blackPawn, e6);

            board.move(whiteKnight, e6);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, NO_CARD_PARAM));
        }

        @Test
        void should_fail_if_attacker_is_a_king() {
            BlackPawn blackPawn = new BlackPawn();
            board.add(blackPawn, b2);
            King whiteKing = (King) board.at(a1).getPiece().orElseThrow();

            board.move(whiteKing, b2);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, NO_CARD_PARAM));
        }
    }
}
