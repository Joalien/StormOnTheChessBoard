package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.SerialKillerCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class SerialKillerCardTest {

    ChessBoard board;
    SerialKillerCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new SerialKillerCard();
    }

    @Test
    void should_chain_capture_one_piece() {
        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, d4);
        Knight blackKnight = new Knight(Color.BLACK);
        board.add(blackKnight, e5);
        Bishop blackBishop = new Bishop(Color.BLACK);
        board.add(blackBishop, d6);

        board.move(whitePawn, e5);

        card.playOn(board, new SerialKillerCardParam(whitePawn, List.of(d6)));

        assertEquals(whitePawn, board.at(d6).getPiece().orElse(null));
        assertTrue(board.at(e5).getPiece().isEmpty());
        assertTrue(board.getOutOfTheBoardPieces().contains(blackKnight));
        assertTrue(board.getOutOfTheBoardPieces().contains(blackBishop));
    }

    @Test
    void should_chain_capture_two_pieces() {
        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, c3);
        Pawn blackPawn1 = new Pawn(Color.BLACK);
        board.add(blackPawn1, d4);
        Knight blackKnight = new Knight(Color.BLACK);
        board.add(blackKnight, c5);
        Bishop blackBishop = new Bishop(Color.BLACK);
        board.add(blackBishop, d6);

        board.move(whitePawn, d4);

        card.playOn(board, new SerialKillerCardParam(whitePawn, List.of(c5, d6)));

        assertEquals(whitePawn, board.at(d6).getPiece().orElse(null));
        assertTrue(board.getOutOfTheBoardPieces().contains(blackPawn1));
        assertTrue(board.getOutOfTheBoardPieces().contains(blackKnight));
        assertTrue(board.getOutOfTheBoardPieces().contains(blackBishop));
    }

    @Test
    void should_reject_if_piece_is_not_a_pawn() {
        Knight whiteKnight = new Knight(Color.WHITE);
        board.add(whiteKnight, b1);
        Pawn blackPawn = new Pawn(Color.BLACK);
        board.add(blackPawn, c3);

        board.move(whiteKnight, c3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new SerialKillerCardParam(whiteKnight, List.of(d4))));
    }

    @Test
    void should_reject_if_positions_list_is_empty() {
        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, d4);
        Knight blackKnight = new Knight(Color.BLACK);
        board.add(blackKnight, e5);

        board.move(whitePawn, e5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new SerialKillerCardParam(whitePawn, List.of())));
    }

    @Test
    void should_reject_if_target_is_not_diagonal_forward() {
        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, d4);
        Knight blackKnight = new Knight(Color.BLACK);
        board.add(blackKnight, e5);
        Pawn blackPawn = new Pawn(Color.BLACK);
        board.add(blackPawn, e6);

        board.move(whitePawn, e5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new SerialKillerCardParam(whitePawn, List.of(e6))));
    }

    @Test
    void should_reject_if_no_piece_to_capture_on_target() {
        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, d4);
        Knight blackKnight = new Knight(Color.BLACK);
        board.add(blackKnight, e5);

        board.move(whitePawn, e5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new SerialKillerCardParam(whitePawn, List.of(f6))));
    }

    @Test
    void should_reject_if_target_is_own_piece() {
        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, d4);
        Knight blackKnight = new Knight(Color.BLACK);
        board.add(blackKnight, e5);
        Bishop whiteBishop = new Bishop(Color.WHITE);
        board.add(whiteBishop, d6);

        board.move(whitePawn, e5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new SerialKillerCardParam(whitePawn, List.of(d6))));
    }

    @Test
    void should_reject_if_target_is_king() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        King blackKing = new King(Color.BLACK);
        board.add(blackKing, d8);
        board.setTurn(Color.WHITE);

        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, d6);
        Bishop blackBishop = new Bishop(Color.BLACK);
        board.add(blackBishop, e7);

        board.move(whitePawn, e7);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new SerialKillerCardParam(whitePawn, List.of(d8))));
    }

    @Test
    void should_work_for_black_pawn() {
        board.setTurn(Color.BLACK);

        Pawn blackPawn = new Pawn(Color.BLACK);
        board.add(blackPawn, d5);
        Knight whiteKnight = new Knight(Color.WHITE);
        board.add(whiteKnight, e4);
        Bishop whiteBishop = new Bishop(Color.WHITE);
        board.add(whiteBishop, d3);

        board.move(blackPawn, e4);

        card.playOn(board, new SerialKillerCardParam(blackPawn, List.of(d3)));

        assertEquals(blackPawn, board.at(d3).getPiece().orElse(null));
        assertTrue(board.getOutOfTheBoardPieces().contains(whiteKnight));
        assertTrue(board.getOutOfTheBoardPieces().contains(whiteBishop));
    }

    @Test
    void should_reject_if_chain_creates_discovered_check() {
        // White king e1, black bishop a5. Diagonal a5-b4-c3-d2-e1.
        // White pawn on b2 captures c3 (normal move), now blocks the diagonal.
        // Card chain-captures d4: pawn leaves c3, bishop a5 sees e1. Discovered check!
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);

        Bishop blackBishop = new Bishop(Color.BLACK);
        board.add(blackBishop, a5);

        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, b2);
        Pawn captureTarget = new Pawn(Color.BLACK);
        board.add(captureTarget, c3);
        board.move(whitePawn, c3);

        Pawn chainTarget = new Pawn(Color.BLACK);
        board.add(chainTarget, d4);

        assertThrows(CheckException.class,
                () -> card.playOn(board, new SerialKillerCardParam(whitePawn, List.of(d4))));
    }

    @Test
    void should_promote_white_pawn_reaching_last_rank() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), a8);
        board.setTurn(Color.WHITE);

        Pawn whitePawn = new Pawn(Color.WHITE);
        board.add(whitePawn, c6);
        Pawn captureSetup = new Pawn(Color.BLACK);
        board.add(captureSetup, d7);
        board.move(whitePawn, d7);

        Knight target = new Knight(Color.BLACK);
        board.add(target, e8);

        card.playOn(board, new SerialKillerCardParam(whitePawn, List.of(e8)));

        Piece pieceOnE8 = board.at(e8).getPiece().orElse(null);
        assertNotNull(pieceOnE8);
        assertInstanceOf(Queen.class, pieceOnE8);
        assertEquals(Color.WHITE, pieceOnE8.getColor());
    }

    @Test
    void should_promote_black_pawn_reaching_first_rank() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), a8);
        board.setTurn(Color.BLACK);

        Pawn blackPawn = new Pawn(Color.BLACK);
        board.add(blackPawn, c3);
        Knight whiteKnight = new Knight(Color.WHITE);
        board.add(whiteKnight, d2);
        board.move(blackPawn, d2);

        Bishop whiteBishop = new Bishop(Color.WHITE);
        board.add(whiteBishop, e1);

        card.playOn(board, new SerialKillerCardParam(blackPawn, List.of(e1)));

        Piece pieceOnE1 = board.at(e1).getPiece().orElse(null);
        assertNotNull(pieceOnE1);
        assertInstanceOf(Queen.class, pieceOnE1);
        assertEquals(Color.BLACK, pieceOnE1.getColor());
    }
}
