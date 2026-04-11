package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.ChargeCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Queen;
import fr.kubys.piece.Square;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ChargeCardTest {

    @Nested
    class Success {
        @Test
        void should_move_two_pawns_forward() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new Pawn(Color.WHITE);
            Pawn pawn2 = new Pawn(Color.WHITE);
            chessBoard.add(pawn1, e4);
            chessBoard.add(pawn2, d4);
            ChargeCard chargeCard = new ChargeCard();
chessBoard.setTurn(Color.WHITE);

            assertDoesNotThrow(() -> chargeCard.playOn(chessBoard, new ChargeCardParam(Set.of(pawn1, pawn2))));

            assertEquals(pawn1, chessBoard.at(e5).getPiece().get());
            assertEquals(pawn2, chessBoard.at(d5).getPiece().get());
        }

        @Test
        void should_move_three_white_pawns_in_front_of_each_other() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new Pawn(Color.WHITE);
            Pawn pawn2 = new Pawn(Color.WHITE);
            Pawn pawn3 = new Pawn(Color.WHITE);
            chessBoard.add(pawn1, e4);
            chessBoard.add(pawn2, e5);
            chessBoard.add(pawn3, e6);
            ChargeCard chargeCard = new ChargeCard();
chessBoard.setTurn(Color.WHITE);

            assertDoesNotThrow(() -> chargeCard.playOn(chessBoard, new ChargeCardParam(Set.of(pawn1, pawn2, pawn3))));

            assertEquals(pawn1, chessBoard.at(e5).getPiece().get());
            assertEquals(pawn2, chessBoard.at(e6).getPiece().get());
            assertEquals(pawn3, chessBoard.at(e7).getPiece().get());
        }

        @Test
        void should_move_three_black_pawns_in_front_of_each_other() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new Pawn(Color.BLACK);
            Pawn pawn2 = new Pawn(Color.BLACK);
            Pawn pawn3 = new Pawn(Color.BLACK);
            chessBoard.add(pawn1, e4);
            chessBoard.add(pawn2, e5);
            chessBoard.add(pawn3, e6);
            ChargeCard chargeCard = new ChargeCard();
            chessBoard.setTurn(Color.BLACK);

            assertDoesNotThrow(() -> chargeCard.playOn(chessBoard, new ChargeCardParam(Set.of(pawn1, pawn2, pawn3))));

            assertEquals(pawn1, chessBoard.at(e3).getPiece().get());
            assertEquals(pawn2, chessBoard.at(e4).getPiece().get());
            assertEquals(pawn3, chessBoard.at(e5).getPiece().get());
        }

        @Test
        void should_move_all_movable_pawns() {
            ChessBoard chessBoard = ChessBoard.createWithInitialState();
            chessBoard.add(new Queen(Color.WHITE), e6);
            Set<Pawn> allBlackPawnsExceptE7 = chessBoard.allyPieces(Color.BLACK).stream()
                    .filter(Pawn.class::isInstance)
                    .map(Pawn.class::cast)
                    .filter(pawn -> !e7.equals(pawn.getPosition()))
                    .collect(Collectors.toSet());
            assertEquals(7, allBlackPawnsExceptE7.size());
            ChargeCard chargeCard = new ChargeCard();
            chessBoard.setTurn(Color.BLACK);

            assertDoesNotThrow(() -> chargeCard.playOn(chessBoard, new ChargeCardParam(allBlackPawnsExceptE7)));

            assertTrue(Set.of(a6, b6, c6, d6, e7, f6, g6, h6).stream()

                    .map(chessBoard::at)
                    .map(Square::getPiece)
                    .map(Optional::get)
                    .allMatch(Pawn.class::isInstance));
        }
    }

    @Nested
    class Promotion {
        @Test
        void white_pawn_on_row_7_should_promote_when_charged() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.WHITE);
            chessBoard.add(pawn, e7);
            chessBoard.setTurn(Color.WHITE);

            new ChargeCard().playOn(chessBoard, new ChargeCardParam(Set.of(pawn)));

            assertInstanceOf(Queen.class, chessBoard.at(e8).getPiece().get());
            assertEquals(Color.WHITE, chessBoard.at(e8).getPiece().get().getColor());
            assertTrue(chessBoard.at(e7).getPiece().isEmpty());
        }

        @Test
        void black_pawn_on_row_2_should_promote_when_charged() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.BLACK);
            chessBoard.add(pawn, e2);
            chessBoard.setTurn(Color.BLACK);

            new ChargeCard().playOn(chessBoard, new ChargeCardParam(Set.of(pawn)));

            assertInstanceOf(Queen.class, chessBoard.at(e1).getPiece().get());
            assertEquals(Color.BLACK, chessBoard.at(e1).getPiece().get().getColor());
            assertTrue(chessBoard.at(e2).getPiece().isEmpty());
        }

        @Test
        void three_white_pawns_on_row_7_should_all_promote_when_charged() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new Pawn(Color.WHITE);
            Pawn pawn2 = new Pawn(Color.WHITE);
            Pawn pawn3 = new Pawn(Color.WHITE);
            chessBoard.add(pawn1, c7);
            chessBoard.add(pawn2, e7);
            chessBoard.add(pawn3, g7);
            chessBoard.setTurn(Color.WHITE);

            new ChargeCard().playOn(chessBoard, new ChargeCardParam(Set.of(pawn1, pawn2, pawn3)));

            assertInstanceOf(Queen.class, chessBoard.at(c8).getPiece().get());
            assertEquals(Color.WHITE, chessBoard.at(c8).getPiece().get().getColor());
            assertInstanceOf(Queen.class, chessBoard.at(e8).getPiece().get());
            assertEquals(Color.WHITE, chessBoard.at(e8).getPiece().get().getColor());
            assertInstanceOf(Queen.class, chessBoard.at(g8).getPiece().get());
            assertEquals(Color.WHITE, chessBoard.at(g8).getPiece().get().getColor());
            assertTrue(chessBoard.at(c7).getPiece().isEmpty());
            assertTrue(chessBoard.at(e7).getPiece().isEmpty());
            assertTrue(chessBoard.at(g7).getPiece().isEmpty());
        }
    }

    @Nested
    class Failure {

        @Test
        void should_fail_if_no_pawn_selected() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            ChargeCard chargeCard = new ChargeCard();
chessBoard.setTurn(Color.WHITE);

            assertThrows(IllegalArgumentException.class, () -> chargeCard.playOn(chessBoard, new ChargeCardParam(Collections.emptySet())));
        }

        @Test
        void should_fail_if_pawn_cannot_move() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn = new Pawn(Color.WHITE);
            Queen queen = new Queen(Color.WHITE);
            chessBoard.add(pawn, e4);
            chessBoard.add(queen, e5);
            ChargeCard chargeCard = new ChargeCard();
chessBoard.setTurn(Color.WHITE);

            assertThrows(IllegalArgumentException.class, () -> chargeCard.playOn(chessBoard, new ChargeCardParam(Set.of(pawn))));
        }

        @Test
        void should_fail_if_only_one_pawn_cannot_move() {
            ChessBoard chessBoard = ChessBoard.createWithInitialState();
            chessBoard.add(new Queen(Color.WHITE), e6);
            Set<Pawn> allBlackPawns = chessBoard.allyPieces(Color.BLACK).stream()
                    .filter(Pawn.class::isInstance)
                    .map(Pawn.class::cast)
                    .collect(Collectors.toSet());
            ChargeCard chargeCard = new ChargeCard();
            chessBoard.setTurn(Color.BLACK);

            assertThrows(IllegalArgumentException.class, () -> chargeCard.playOn(chessBoard, new ChargeCardParam(allBlackPawns)));
        }

        @Test
        void should_not_move_pawns_of_enemy_color() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn1 = new Pawn(Color.WHITE);
            Pawn pawn2 = new Pawn(Color.BLACK);
            chessBoard.add(pawn1, e4);
            chessBoard.add(pawn2, g3);
            ChargeCard chargeCard = new ChargeCard();
chessBoard.setTurn(Color.WHITE);

            assertThrows(CannotMoveThisColorException.class, () -> chargeCard.playOn(chessBoard, new ChargeCardParam(Set.of(pawn1, pawn2))));

            assertEquals(pawn1, chessBoard.at(e4).getPiece().get());
            assertEquals(pawn2, chessBoard.at(g3).getPiece().get());
        }

        @Test
        void should_not_move_pawns_of_enemy_color_bis() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn2 = new Pawn(Color.WHITE);
            chessBoard.add(pawn2, g5);
            ChargeCard chargeCard = new ChargeCard();
            chessBoard.setTurn(Color.BLACK);

            assertThrows(CannotMoveThisColorException.class, () -> chargeCard.playOn(chessBoard, new ChargeCardParam(Set.of(pawn2))));

            assertEquals(pawn2, chessBoard.at(g5).getPiece().get());
        }
    }
}