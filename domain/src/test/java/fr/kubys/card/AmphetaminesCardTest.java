package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class AmphetaminesCardTest {

    ChessBoard board;
    AmphetaminesCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new AmphetaminesCard();
    }

    @Test
    void should_move_bishop_again() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, c1);
        board.move(bishop, d2);

        card.playOn(board, new PieceToPositionCardParam(bishop, f4));

        assertEquals(bishop, board.at(f4).getPiece().get());
    }

    @Test
    void should_reject_if_bishop_captured() {
        Bishop bishop = new Bishop(Color.WHITE);
        board.add(bishop, c1);
        Pawn enemyPawn = new Pawn(Color.BLACK);
        board.add(enemyPawn, d2);
        board.move(bishop, d2);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(bishop, f4)));
    }

    @Test
    void should_reject_non_bishop() {
        Knight knight = new Knight(Color.WHITE);
        board.add(knight, b1);
        board.move(knight, c3);

        assertThrows(IllegalArgumentException.class,
                () -> card.playOn(board, new PieceToPositionCardParam(knight, d5)));
    }
}
