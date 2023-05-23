package card;

import board.ChessBoard;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.BlackPawn;
import piece.Pawn;
import piece.WhitePawn;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static position.Position.*;

class LightweightSquadCardTest {

    @Nested
    class Success {

        @Test
        void should_move_two_pawns() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new WhitePawn();
            Pawn pawn2 = new WhitePawn();
            chessBoard.add(pawn1, g1);
            chessBoard.add(pawn2, e4);
            Card lightweightSquadCard = new LightweightSquadCard();

            assertTrue(lightweightSquadCard.playOn(chessBoard, List.of(pawn1, pawn2)));

            assertTrue(chessBoard.at(g1).getPiece().isEmpty());
            assertEquals(pawn1, chessBoard.at(g3).getPiece().get());
            assertTrue(chessBoard.at(e4).getPiece().isEmpty());
            assertEquals(pawn2, chessBoard.at(e6).getPiece().get());
        }

        @Test
        void pawns_in_front_of_each_other() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new BlackPawn();
            Pawn pawn2 = new BlackPawn();
            chessBoard.add(pawn1, a5);
            chessBoard.add(pawn2, a4);
            Card lightweightSquadCard = new LightweightSquadCard();

            assertTrue(lightweightSquadCard.playOn(chessBoard, List.of(pawn1, pawn2)));

            assertTrue(chessBoard.at(a5).getPiece().isEmpty());
            assertEquals(pawn1, chessBoard.at(a3).getPiece().get());
            assertTrue(chessBoard.at(a4).getPiece().isEmpty());
            assertEquals(pawn2, chessBoard.at(a2).getPiece().get());
        }

        @Test
        void pawns_in_front_of_each_other_bis() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new BlackPawn();
            Pawn pawn2 = new BlackPawn();
            chessBoard.add(pawn1, a5);
            chessBoard.add(pawn2, a4);
            LightweightSquadCard lightweightSquadCard = new LightweightSquadCard();

            assertTrue(lightweightSquadCard.playOn(chessBoard, List.of(pawn1, pawn2)));

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
            Pawn pawn1 = new WhitePawn();
            Pawn pawn2 = new BlackPawn();
            chessBoard.add(pawn1, e4);
            chessBoard.add(pawn2, g1);
            Card lightweightSquadCard = new LightweightSquadCard();

            assertThrows(IllegalArgumentException.class, () -> lightweightSquadCard.playOn(chessBoard, List.of(pawn1, pawn2)));

            assertEquals(pawn1, chessBoard.at(e4).getPiece().get());
            assertEquals(pawn2, chessBoard.at(g1).getPiece().get());
        }

        @Test
        void should_not_be_the_same_pawns() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new WhitePawn();
            chessBoard.add(pawn1, e4);
            LightweightSquadCard lightweightSquadCard = new LightweightSquadCard();

            assertThrows(IllegalArgumentException.class, () -> lightweightSquadCard.playOn(chessBoard, List.of(pawn1, pawn1)));

            assertEquals(pawn1, chessBoard.at(e4).getPiece().get());
        }

        @Test
        @Disabled
        void promotion() {

        }


    }
}