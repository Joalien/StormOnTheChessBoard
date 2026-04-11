package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.KnightCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class MadHorseDiseaseCardTest {

    ChessBoard board;
    MadHorseDiseaseCard card;

    @BeforeEach
    void setUp() {
        board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);
        card = new MadHorseDiseaseCard();
    }

    @Nested
    class Success {
        @Test
        void should_transform_own_knight_into_bishop() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, b1);

            card.playOn(board, new KnightCardParam(knight));

            assertInstanceOf(Bishop.class, board.at(b1).getPiece().get());
            assertEquals(Color.WHITE, board.at(b1).getPiece().get().getColor());
            assertTrue(board.getOutOfTheBoardPieces().contains(knight));
        }

        @Test
        void should_transform_enemy_knight_into_bishop() {
            Knight knight = new Knight(Color.BLACK);
            board.add(knight, g8);

            card.playOn(board, new KnightCardParam(knight));

            assertInstanceOf(Bishop.class, board.at(g8).getPiece().get());
            assertEquals(Color.BLACK, board.at(g8).getPiece().get().getColor());
        }
    }

    @Nested
    class Failure {
        @Test
        void should_reject_if_knight_not_on_board() {
            Knight knight = new Knight(Color.WHITE);
            board.add(knight, b1);
            board.removePieceFromTheBoard(knight);

            assertThrows(IllegalArgumentException.class,
                    () -> card.playOn(board, new KnightCardParam(knight)));
        }

        @Test
        void should_reject_if_enemy_knight_becomes_bishop_checking_own_king() {
            // Black knight on f3 → becomes bishop on f3, which checks white king on e2
            board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), d4);
            board.add(new King(Color.BLACK), e8);
            Knight knight = new Knight(Color.BLACK);
            board.add(knight, f6);
            board.setTurn(Color.WHITE);

            // Bishop on f6 would check king on d4 via diagonal
            assertThrows(CheckException.class,
                    () -> card.playOn(board, new KnightCardParam(knight)));
        }

        @Test
        void should_allow_when_enemy_knight_becomes_bishop_not_checking() {
            Knight knight = new Knight(Color.BLACK);
            board.add(knight, a3);

            // Bishop on a3 does not check white king on e1
            assertDoesNotThrow(() -> card.playOn(board, new KnightCardParam(knight)));
        }
    }
}
