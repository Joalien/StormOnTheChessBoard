package card;

import board.ChessBoard;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import piece.*;
import position.Square;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ChargeCardTest {

    @Nested
    class Success {
        @Test
        void should_move_two_pawns_forward() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new WhitePawn();
            Pawn pawn2 = new WhitePawn();
            chessBoard.add(pawn1, "e4");
            chessBoard.add(pawn2, "d4");
            SCCard chargeCard = new ChargeCard(Set.of(pawn1, pawn2));

            assertTrue(chessBoard.playCard(chargeCard));

            assertEquals(pawn1, chessBoard.at("e5").getPiece().get());
            assertEquals(pawn2, chessBoard.at("d5").getPiece().get());
        }

        @Test
        void should_move_three_white_pawns_in_front_of_each_other() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new WhitePawn();
            Pawn pawn2 = new WhitePawn();
            Pawn pawn3 = new WhitePawn();
            chessBoard.add(pawn1, "e4");
            chessBoard.add(pawn2, "e5");
            chessBoard.add(pawn3, "e6");
            SCCard chargeCard = new ChargeCard(Set.of(pawn1, pawn2, pawn3));

            assertTrue(chessBoard.playCard(chargeCard));

            assertEquals(pawn1, chessBoard.at("e5").getPiece().get());
            assertEquals(pawn2, chessBoard.at("e6").getPiece().get());
            assertEquals(pawn3, chessBoard.at("e7").getPiece().get());
        }

        @Test
        void should_move_three_black_pawns_in_front_of_each_other() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new BlackPawn();
            Pawn pawn2 = new BlackPawn();
            Pawn pawn3 = new BlackPawn();
            chessBoard.add(pawn1, "e4");
            chessBoard.add(pawn2, "e5");
            chessBoard.add(pawn3, "e6");
            SCCard chargeCard = new ChargeCard(Set.of(pawn1, pawn2, pawn3));

            assertTrue(chessBoard.playCard(chargeCard));

            assertEquals(pawn1, chessBoard.at("e3").getPiece().get());
            assertEquals(pawn2, chessBoard.at("e4").getPiece().get());
            assertEquals(pawn3, chessBoard.at("e5").getPiece().get());
        }

        @Test
        void should_move_all_movable_pawns() {
            ChessBoard chessBoard = ChessBoard.createWithInitialState();
            chessBoard.add(new Queen(Color.WHITE), "e6");
            Set<Pawn> allBlackPawnsExceptE7 = chessBoard.allyPieces(Color.BLACK).stream()
                    .filter(Pawn.class::isInstance)
                    .map(Pawn.class::cast)
                    .filter(pawn -> !"e7".equals(pawn.getPosition()))
                    .collect(Collectors.toSet());
            assertEquals(7, allBlackPawnsExceptE7.size());
            SCCard chargeCard = new ChargeCard(allBlackPawnsExceptE7);

            assertTrue(chessBoard.playCard(chargeCard));

            assertTrue(Set.of("a6", "b6", "c6", "d6", "e7", "f6", "g6", "h6").stream()
                    .map(chessBoard::at)
                    .map(Square::getPiece)
                    .map(Optional::get)
                    .allMatch(Pawn.class::isInstance));
        }
    }

    @Nested
    class Failure {

        @Test
        void should_fail_if_no_pawn_selected() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            SCCard chargeCard = new ChargeCard(Collections.emptySet());

            assertThrows(IllegalArgumentException.class, () -> chessBoard.playCard(chargeCard));
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

            assertThrows(IllegalArgumentException.class, () -> chessBoard.playCard(chargeCard));
        }

        @Test
        void should_fail_if_only_one_pawn_cannot_move() {
            ChessBoard chessBoard = ChessBoard.createWithInitialState();
            chessBoard.add(new Queen(Color.WHITE), "e6");
            Set<Pawn> allBlackPawns = chessBoard.allyPieces(Color.BLACK).stream()
                    .filter(Pawn.class::isInstance)
                    .map(Pawn.class::cast)
                    .collect(Collectors.toSet());
            SCCard chargeCard = new ChargeCard(allBlackPawns);

            assertThrows(IllegalArgumentException.class, () -> chessBoard.playCard(chargeCard));
        }

        @Test
        void should_not_move_pawns_of_different_color() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new WhitePawn();
            Pawn pawn2 = new BlackPawn();
            String e4 = "e4";
            String g1 = "g1";
            chessBoard.add(pawn1, e4);
            chessBoard.add(pawn2, g1);
            SCCard lightweightSquadCard = new LightweightSquadCard(pawn1, pawn2);

            assertThrows(IllegalArgumentException.class, () -> chessBoard.playCard(lightweightSquadCard));

            assertEquals(pawn1, chessBoard.at(e4).getPiece().get());
            assertEquals(pawn2, chessBoard.at(g1).getPiece().get());
        }
    }
}