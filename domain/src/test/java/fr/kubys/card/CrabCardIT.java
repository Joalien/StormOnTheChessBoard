package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CrabCardIT {

    @Test
    void should_promote_white_crab_to_queen_when_reaching_row_8() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        Crab crab = new Crab(Color.WHITE);
        board.add(crab, d7);
        board.setTurn(Color.WHITE);

        board.tryToMove(d7, e8);

        assertInstanceOf(Queen.class, board.at(e8).getPiece().get());
        assertEquals(Color.WHITE, board.at(e8).getPiece().get().getColor());
    }

    @Test
    void should_promote_black_crab_to_queen_when_reaching_row_1() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        Crab crab = new Crab(Color.BLACK);
        board.add(crab, e2);
        board.setTurn(Color.BLACK);

        board.tryToMove(e2, d1);

        assertInstanceOf(Queen.class, board.at(d1).getPiece().get());
        assertEquals(Color.BLACK, board.at(d1).getPiece().get().getColor());
    }

    @Test
    void should_support_under_promotion_after_crab_promotion() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        Crab crab = new Crab(Color.WHITE);
        board.add(crab, c7);
        board.setTurn(Color.WHITE);

        board.tryToMove(c7, d8);
        board.overridePromotion(d8, PromotionPiece.KNIGHT);

        assertInstanceOf(Knight.class, board.at(d8).getPiece().get());
        assertEquals(Color.WHITE, board.at(d8).getPiece().get().getColor());
    }

    @Test
    void should_transform_pawn_then_promote_crab() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        WhitePawn pawn = new WhitePawn();
        board.add(pawn, e6);
        board.setTurn(Color.WHITE);

        // Transform pawn into crab
        new CrabCard().playOn(board, new PieceCardParam(pawn));
        Piece crab = board.at(e6).getPiece().get();
        assertInstanceOf(Crab.class, crab);

        // Move crab diagonally to 7th rank
        board.tryToMove(e6, f7);
        assertInstanceOf(Crab.class, board.at(f7).getPiece().get());

        // Move crab to 8th rank - should promote
        board.tryToMove(f7, g8);
        assertInstanceOf(Queen.class, board.at(g8).getPiece().get());
    }

    @Test
    void should_promote_crab_by_capturing_on_last_rank() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        Crab crab = new Crab(Color.WHITE);
        board.add(crab, d7);
        Knight enemy = new Knight(Color.BLACK);
        board.add(enemy, e8);
        board.setTurn(Color.WHITE);

        board.tryToMove(d7, e8);

        assertInstanceOf(Queen.class, board.at(e8).getPiece().get());
        assertTrue(board.getOutOfTheBoardPieces().contains(enemy));
    }
}
