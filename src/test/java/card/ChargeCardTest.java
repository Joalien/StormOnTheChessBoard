package card;

import board.ChessBoard;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.Color;
import piece.Pawn;
import piece.Queen;
import piece.WhitePawn;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ChargeCardTest {

    @Nested
    class Success {
        @Test
        void should_move_two_pawns_forward() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new WhitePawn();
            Pawn pawn2 = new WhitePawn();
            String e4 = "e4";
            String d4 = "d4";
            chessBoard.add(pawn1, e4);
            chessBoard.add(pawn2, d4);
            SCCard chargeCard = new ChargeCard(Set.of(pawn1, pawn2));

            assertTrue(chessBoard.play(chargeCard));

            assertEquals(pawn1, chessBoard.at("e5").getPiece().get());
            assertEquals(pawn2, chessBoard.at("d5").getPiece().get());
        }
    }

    @Nested
    class Failure {

        @Test
        void should_fail_if_no_pawn_selected() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            SCCard chargeCard = new ChargeCard(Collections.emptySet());

            assertThrows(IllegalArgumentException.class, () -> chessBoard.play(chargeCard));
        }

        @Test
        void should_fail_if_pawn_cannot_move() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new WhitePawn();
            Queen queen = new Queen(Color.WHITE);
            String e4 = "e4";
            String e5 = "e5";
            chessBoard.add(pawn, e4);
            chessBoard.add(queen, e5);
            SCCard chargeCard = new ChargeCard(Set.of(pawn));

            assertThrows(IllegalArgumentException.class, () -> chessBoard.play(chargeCard));
        }
    }
}