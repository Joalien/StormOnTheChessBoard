package fr.kubys.board.effect;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ManHoleEffectTest {

    private ChessBoard chessBoard;
    private ManHoleEffect manHoleEffect;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createWithInitialState();
    }

    @Test
    void should_move_from_one_hole_to_the_other() {
        manHoleEffect = new ManHoleEffect(c2, e6);
        chessBoard.addEffect(manHoleEffect);

        assertTrue(chessBoard.canMove(chessBoard.at(c2).getPiece().get(), e6));
    }

    @Test
    void should_move_from_one_hole_to_the_other_bis() {
        manHoleEffect = new ManHoleEffect(e6, c2);
        chessBoard.addEffect(manHoleEffect);

        assertTrue(chessBoard.canMove(chessBoard.at(c2).getPiece().get(), e6));
    }

    @Test
    void should_move_from_one_hole_to_the_other_and_take_an_enemy_piece() {
        manHoleEffect = new ManHoleEffect(c2, e6);
        Piece piece = chessBoard.at(e7).getPiece().get();
        chessBoard.tryToMove(piece, e6);
        chessBoard.addEffect(manHoleEffect);

        assertTrue(chessBoard.canMove(chessBoard.at(c2).getPiece().get(), e6));
        assertDoesNotThrow(() -> chessBoard.tryToMove(chessBoard.at(c2).getPiece().get(), e6));
        assertTrue(chessBoard.getOutOfTheBoardPieces().contains(piece));
    }

    @Test
    void should_move_from_one_hole_to_the_other_even_if_theorical_path_is_blocked() {
        manHoleEffect = new ManHoleEffect(d1, d8);
        chessBoard.addEffect(manHoleEffect);
        Piece queen = chessBoard.at(d8).getPiece().get();

        assertTrue(chessBoard.canMove(chessBoard.at(d1).getPiece().get(), d8));
        assertDoesNotThrow(() -> chessBoard.tryToMove(chessBoard.at(d1).getPiece().get(), d8));
        assertTrue(chessBoard.getOutOfTheBoardPieces().contains(queen));
    }

    @Test
    void should_not_be_able_to_move_king_on_the_other_man_hole() {
        manHoleEffect = new ManHoleEffect(c2, e6);
        chessBoard.removePieceFromTheBoard(chessBoard.at(e7).getPiece().get());
        Piece blackKing = chessBoard.at(e8).getPiece().get();
        assertDoesNotThrow(() -> chessBoard.tryToMove(blackKing, e7));
        assertTrue(chessBoard.canMove(blackKing, e6));

        chessBoard.addEffect(manHoleEffect);

        assertThrows(CheckException.class, () -> chessBoard.canMove(blackKing, e6));
        assertThrows(CheckException.class, () -> chessBoard.tryToMove(blackKing, e6));
    }

    @Test
    void white_pawn_should_promote_when_teleporting_to_row_8_via_manhole() {
        ChessBoard board = ChessBoard.createEmpty();
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, c4);
        board.addEffect(new ManHoleEffect(c4, c8));

        board.tryToMove(pawn, c8);

        assertInstanceOf(Queen.class, board.at(c8).getPiece().get());
        assertEquals(Color.WHITE, board.at(c8).getPiece().get().getColor());
        assertTrue(board.at(c4).getPiece().isEmpty());
    }

    @Test
    void black_pawn_should_promote_when_teleporting_to_row_1_via_manhole() {
        ChessBoard board = ChessBoard.createEmpty();
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, f5);
        board.addEffect(new ManHoleEffect(f5, f1));

        board.tryToMove(pawn, f1);

        assertInstanceOf(Queen.class, board.at(f1).getPiece().get());
        assertEquals(Color.BLACK, board.at(f1).getPiece().get().getColor());
        assertTrue(board.at(f5).getPiece().isEmpty());
    }

    @Test
    void should_not_be_able_to_move_king_on_the_other_man_hole_if_first_one_is_empty() {
        manHoleEffect = new ManHoleEffect(e4, e6);
        chessBoard.removePieceFromTheBoard(chessBoard.at(e7).getPiece().get());
        Piece blackKing = chessBoard.at(e8).getPiece().get();
        assertDoesNotThrow(() -> chessBoard.tryToMove(blackKing, e7));
        assertTrue(chessBoard.canMove(blackKing, e6));

        chessBoard.addEffect(manHoleEffect);

        assertDoesNotThrow(() -> chessBoard.tryToMove(blackKing, e6));
        assertDoesNotThrow(() -> chessBoard.tryToMove(blackKing, e4));
    }
}