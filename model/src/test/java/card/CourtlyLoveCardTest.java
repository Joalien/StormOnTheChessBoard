package card;

import board.CheckException;
import board.ChessBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourtlyLoveCardTest {

    public static final String b4 = "b4";
    public static final String e1 = "e1";
    public static final String d1 = "d1";
    public static final String c1 = "c1";
    public static final String d2 = "d2";
    private Knight knight;
    private Card courtlyLoveCard;
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

    @Nested
    class Success {
        @Test
        void should_tp_knight_to_c1() {
            assertTrue(courtlyLoveCard.playOn(chessBoard, List.of(knight, c1)));

            assertEquals(knight, chessBoard.at(c1).getPiece().get());
            assertTrue(chessBoard.at(b4).getPiece().isEmpty());
        }

        @Test
        void should_tp_knight_to_protect_from_chess() {
            chessBoard.add(new Bishop(Color.BLACK), "a5");

            assertTrue(courtlyLoveCard.playOn(chessBoard, List.of(knight, d2)));

            assertEquals(knight, chessBoard.at(d2).getPiece().get());
            assertTrue(chessBoard.at(b4).getPiece().isEmpty());
        }

        @Test
        void should_tp_knight_to_protect_from_chess_bis() {
            chessBoard.add(new Bishop(Color.BLACK), "c3");

            assertTrue(courtlyLoveCard.playOn(chessBoard, List.of(knight, d2)));

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

            assertThrows(IllegalArgumentException.class, () -> courtlyLoveCard.playOn(chessBoard, List.of(knight, d2)));

            assertEquals(knight, chessBoard.at(b4).getPiece().get());
            assertEquals(bishop, chessBoard.at(d2).getPiece().get());
        }

        @Test
        void should_not_tp_if_it_create_check() {
            chessBoard.add(new Bishop(Color.BLACK), "a5");

            assertThrows(CheckException.class, () -> createCourtlyLoveCard().playOn(chessBoard, List.of(knight, c1)));

            assertEquals(knight, chessBoard.at(b4).getPiece().get());
            assertTrue(chessBoard.at(c1).getPiece().isEmpty());
        }

        @Test
        void should_not_tp_if_position_not_nearby_queen() {
            String h8 = "h8";
            assertThrows(IllegalArgumentException.class, () -> createCourtlyLoveCard().playOn(chessBoard, List.of(knight, h8)));

            assertEquals(knight, chessBoard.at(b4).getPiece().get());
            assertTrue(chessBoard.at(h8).getPiece().isEmpty());
        }

        @Test
        void should_not_move_enemy_knight() {
            Card courtlyLoveCard = createCourtlyLoveCard();
            courtlyLoveCard.setIsPlayedBy(Color.BLACK);
            assertThrows(IllegalColorException.class, () -> courtlyLoveCard.playOn(chessBoard, List.of(knight, c1)));

            assertEquals(knight, chessBoard.at(b4).getPiece().get());
            assertTrue(chessBoard.at(c1).getPiece().isEmpty());
        }
    }

    private CourtlyLoveCard createCourtlyLoveCard() {
        CourtlyLoveCard courtlyLoveCard1 = new CourtlyLoveCard();
        courtlyLoveCard1.setIsPlayedBy(Color.WHITE);
        return courtlyLoveCard1;
    }
}