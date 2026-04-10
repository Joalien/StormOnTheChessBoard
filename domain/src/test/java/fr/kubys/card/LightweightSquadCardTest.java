package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.LightweightSquadCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class LightweightSquadCardTest {

    @Nested
    class Success {

        @Test
        void should_move_two_pawns() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new Pawn(Color.WHITE);
            Pawn pawn2 = new Pawn(Color.WHITE);
            chessBoard.add(pawn1, g1);
            chessBoard.add(pawn2, e4);
            LightweightSquadCard lightweightSquadCard = new LightweightSquadCard();

            assertDoesNotThrow(() -> lightweightSquadCard.playOn(chessBoard, new LightweightSquadCardParam(pawn1, pawn2)));

            assertTrue(chessBoard.at(g1).getPiece().isEmpty());
            assertEquals(pawn1, chessBoard.at(g3).getPiece().get());
            assertTrue(chessBoard.at(e4).getPiece().isEmpty());
            assertEquals(pawn2, chessBoard.at(e6).getPiece().get());
        }

        @Test
        void pawns_in_front_of_each_other() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new Pawn(Color.BLACK);
            Pawn pawn2 = new Pawn(Color.BLACK);
            chessBoard.add(pawn1, a5);
            chessBoard.add(pawn2, a4);
            LightweightSquadCard lightweightSquadCard = new LightweightSquadCard();

            assertDoesNotThrow(() -> lightweightSquadCard.playOn(chessBoard, new LightweightSquadCardParam(pawn1, pawn2)));

            assertTrue(chessBoard.at(a5).getPiece().isEmpty());
            assertEquals(pawn1, chessBoard.at(a3).getPiece().get());
            assertTrue(chessBoard.at(a4).getPiece().isEmpty());
            assertEquals(pawn2, chessBoard.at(a2).getPiece().get());
        }

        @Test
        void pawns_in_front_of_each_other_bis() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new Pawn(Color.BLACK);
            Pawn pawn2 = new Pawn(Color.BLACK);
            chessBoard.add(pawn1, a5);
            chessBoard.add(pawn2, a4);
            LightweightSquadCard lightweightSquadCard = new LightweightSquadCard();

            assertDoesNotThrow(() -> lightweightSquadCard.playOn(chessBoard, new LightweightSquadCardParam(pawn1, pawn2)));

            assertTrue(chessBoard.at(a5).getPiece().isEmpty());
            assertEquals(pawn1, chessBoard.at(a3).getPiece().get());
            assertTrue(chessBoard.at(a4).getPiece().isEmpty());
            assertEquals(pawn2, chessBoard.at(a2).getPiece().get());
        }
    }

    @Nested
    class Failure {

        @Test
        void should_not_move_pawns_of_different_color() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new Pawn(Color.WHITE);
            Pawn pawn2 = new Pawn(Color.BLACK);
            chessBoard.add(pawn1, e4);
            chessBoard.add(pawn2, g3);
            LightweightSquadCard lightweightSquadCard = new LightweightSquadCard();

            assertThrows(IllegalArgumentException.class, () -> lightweightSquadCard.playOn(chessBoard, new LightweightSquadCardParam(pawn1, pawn2)));

            assertEquals(pawn1, chessBoard.at(e4).getPiece().get());
            assertEquals(pawn2, chessBoard.at(g3).getPiece().get());
        }

        @Test
        void should_not_be_on_second_to_last_row() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new Pawn(Color.WHITE);
            Pawn pawn2 = new Pawn(Color.WHITE);
            chessBoard.add(pawn1, d7);
            chessBoard.add(pawn2, e7);
            LightweightSquadCard lightweightSquadCard = new LightweightSquadCard();

            assertThrows(IllegalArgumentException.class, () -> lightweightSquadCard.playOn(chessBoard, new LightweightSquadCardParam(pawn1, pawn2)));

            assertEquals(pawn1, chessBoard.at(d7).getPiece().get());
            assertEquals(pawn2, chessBoard.at(e7).getPiece().get());
        }

        @Test
        void white_pawn_on_row_6_should_promote_when_advanced_two_squares() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn promotingPawn = new Pawn(Color.WHITE);
            Pawn otherPawn = new Pawn(Color.WHITE);
            chessBoard.add(promotingPawn, e6);
            chessBoard.add(otherPawn, d4);

            new LightweightSquadCard().playOn(chessBoard, new LightweightSquadCardParam(promotingPawn, otherPawn));

            assertInstanceOf(Queen.class, chessBoard.at(e8).getPiece().get());
            assertEquals(Color.WHITE, chessBoard.at(e8).getPiece().get().getColor());
            assertTrue(chessBoard.at(e6).getPiece().isEmpty());
            assertInstanceOf(Pawn.class, chessBoard.at(d6).getPiece().get());
        }

        @Test
        void black_pawn_on_row_3_should_promote_when_advanced_two_squares() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn promotingPawn = new Pawn(Color.BLACK);
            Pawn otherPawn = new Pawn(Color.BLACK);
            chessBoard.add(promotingPawn, e3);
            chessBoard.add(otherPawn, d5);

            new LightweightSquadCard().playOn(chessBoard, new LightweightSquadCardParam(promotingPawn, otherPawn));

            assertInstanceOf(Queen.class, chessBoard.at(e1).getPiece().get());
            assertEquals(Color.BLACK, chessBoard.at(e1).getPiece().get().getColor());
            assertTrue(chessBoard.at(e3).getPiece().isEmpty());
            assertInstanceOf(Pawn.class, chessBoard.at(d3).getPiece().get());
        }

        @Test
        void both_white_pawns_on_row_6_should_both_promote_when_advanced_two_squares() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new Pawn(Color.WHITE);
            Pawn pawn2 = new Pawn(Color.WHITE);
            chessBoard.add(pawn1, c6);
            chessBoard.add(pawn2, e6);

            new LightweightSquadCard().playOn(chessBoard, new LightweightSquadCardParam(pawn1, pawn2));

            assertInstanceOf(Queen.class, chessBoard.at(c8).getPiece().get());
            assertEquals(Color.WHITE, chessBoard.at(c8).getPiece().get().getColor());
            assertInstanceOf(Queen.class, chessBoard.at(e8).getPiece().get());
            assertEquals(Color.WHITE, chessBoard.at(e8).getPiece().get().getColor());
            assertTrue(chessBoard.at(c6).getPiece().isEmpty());
            assertTrue(chessBoard.at(e6).getPiece().isEmpty());
        }


    }
}