package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class MeritPromotionCardTest {

    ChessBoard board;
    MeritPromotionCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new MeritPromotionCard();
    }

    @Test
    void should_promote_pawn_on_sixth_rank() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d6);

        card.playOn(board, new PieceCardParam(pawn));

        assertInstanceOf(Queen.class, board.at(d6).getPiece().get());
        assertEquals(Color.WHITE, board.at(d6).getPiece().get().getColor());
    }

    @Test
    void should_reject_pawn_not_on_sixth_rank() {
        Pawn pawn = new Pawn(Color.WHITE);
        board.add(pawn, d5);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceCardParam(pawn)));
    }

    @Test
    void should_work_for_black_on_third_rank() {
        Pawn pawn = new Pawn(Color.BLACK);
        board.add(pawn, d3);
        board.setTurn(Color.BLACK);

        card.playOn(board, new PieceCardParam(pawn));

        assertInstanceOf(Queen.class, board.at(d3).getPiece().get());
        assertEquals(Color.BLACK, board.at(d3).getPiece().get().getColor());
    }
}
