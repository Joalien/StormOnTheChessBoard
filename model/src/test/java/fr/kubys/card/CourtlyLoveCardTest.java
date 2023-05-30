package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.CourtlyLoveCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Bishop;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CourtlyLoveCardTest {

    private Knight knight;
    private CourtlyLoveCard courtlyLoveCard;
    private ChessBoard chessBoard;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
        knight = new Knight(Color.WHITE);
        chessBoard.add(knight, b4);
        chessBoard.add(new Queen(Color.WHITE), d1);
        chessBoard.add(new King(Color.WHITE), e1);

        courtlyLoveCard = createCourtlyLoveCard();
    }

    private CourtlyLoveCard createCourtlyLoveCard() {
        CourtlyLoveCard courtlyLoveCard1 = new CourtlyLoveCard();
        courtlyLoveCard1.setIsPlayedBy(Color.WHITE);
        return courtlyLoveCard1;
    }

    @Nested
    class Success {
        @Test
        void should_tp_knight_to_c1() {
            assertDoesNotThrow(() -> courtlyLoveCard.playOn(chessBoard, new CourtlyLoveCardParam(knight, c1)));

            assertEquals(knight, chessBoard.at(c1).getPiece().get());
            assertTrue(chessBoard.at(b4).getPiece().isEmpty());
        }

        @Test
        void should_tp_knight_to_protect_from_chess() {
            chessBoard.add(new Bishop(Color.BLACK), a5);

            assertDoesNotThrow(() -> courtlyLoveCard.playOn(chessBoard, new CourtlyLoveCardParam(knight, d2)));

            assertEquals(knight, chessBoard.at(d2).getPiece().get());
            assertTrue(chessBoard.at(b4).getPiece().isEmpty());
        }

        @Test
        void should_tp_knight_to_protect_from_chess_bis() {
            chessBoard.add(new Bishop(Color.BLACK), c3);

            assertDoesNotThrow(() -> courtlyLoveCard.playOn(chessBoard, new CourtlyLoveCardParam(knight, d2)));

            assertEquals(knight, chessBoard.at(d2).getPiece().get());
            assertTrue(chessBoard.at(b4).getPiece().isEmpty());
        }
    }

    @Nested
    class Failure {

        @Test
        void should_not_tp_if_position_is_occupied() {
            Bishop bishop = new Bishop(Color.BLACK);
            chessBoard.add(bishop, d2);

            assertThrows(IllegalArgumentException.class, () -> courtlyLoveCard.playOn(chessBoard, new CourtlyLoveCardParam(knight, d2)));

            assertEquals(knight, chessBoard.at(b4).getPiece().get());
            assertEquals(bishop, chessBoard.at(d2).getPiece().get());
        }

        @Test
        void should_not_tp_if_it_create_check() {
            chessBoard.add(new Bishop(Color.BLACK), a5);

            assertThrows(CheckException.class, () -> createCourtlyLoveCard().playOn(chessBoard, new CourtlyLoveCardParam(knight, c1)));

            assertEquals(knight, chessBoard.at(b4).getPiece().get());
            assertTrue(chessBoard.at(c1).getPiece().isEmpty());
        }

        @Test
        void should_not_tp_if_position_not_nearby_queen() {
            assertThrows(IllegalArgumentException.class, () -> createCourtlyLoveCard().playOn(chessBoard, new CourtlyLoveCardParam(knight, h8)));

            assertEquals(knight, chessBoard.at(b4).getPiece().get());
            assertTrue(chessBoard.at(h8).getPiece().isEmpty());
        }

        @Test
        void should_not_move_enemy_knight() {
            CourtlyLoveCard courtlyLoveCard = createCourtlyLoveCard();
            courtlyLoveCard.setIsPlayedBy(Color.BLACK);
            assertThrows(CannotMoveThisColorException.class, () -> courtlyLoveCard.playOn(chessBoard, new CourtlyLoveCardParam(knight, c1)));

            assertEquals(knight, chessBoard.at(b4).getPiece().get());
            assertTrue(chessBoard.at(c1).getPiece().isEmpty());
        }
    }
}