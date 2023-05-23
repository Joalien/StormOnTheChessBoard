package effect;

import board.ChessBoard;
import effet.ManHoleEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import piece.Piece;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static position.Position.*;

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
        assertTrue(chessBoard.tryToMove(chessBoard.at(c2).getPiece().get(), e6));
        assertTrue(chessBoard.getOutOfTheBoardPieces().contains(piece));
    }

    @Test
    void should_not_be_able_to_move_king_on_the_other_man_hole() {
        manHoleEffect = new ManHoleEffect(c2, e6);
        chessBoard.removePieceFromTheBoard(chessBoard.at(e7).getPiece().get());
        Piece blackKing = chessBoard.at(e8).getPiece().get();
        assertTrue(chessBoard.tryToMove(blackKing, e7));
        assertTrue(chessBoard.canMove(blackKing, e6));

        chessBoard.addEffect(manHoleEffect);

        assertFalse(chessBoard.canMove(blackKing, e6));
        assertFalse(chessBoard.tryToMove(blackKing, e6));
    }

    @Test
    void should_not_be_able_to_move_king_on_the_other_man_hole_if_first_one_is_empty() {
        manHoleEffect = new ManHoleEffect(e4, e6);
        chessBoard.removePieceFromTheBoard(chessBoard.at(e7).getPiece().get());
        Piece blackKing = chessBoard.at(e8).getPiece().get();
        assertTrue(chessBoard.tryToMove(blackKing, e7));
        assertTrue(chessBoard.canMove(blackKing, e6));

        chessBoard.addEffect(manHoleEffect);

        assertTrue(chessBoard.tryToMove(blackKing, e6));
        assertTrue(chessBoard.tryToMove(blackKing, e4));
    }
}