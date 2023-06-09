package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.ChargeCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
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
            Pawn pawn1 = new WhitePawn();
            Pawn pawn2 = new WhitePawn();
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
            Pawn pawn1 = new WhitePawn();
            Pawn pawn2 = new WhitePawn();
            Pawn pawn3 = new WhitePawn();
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
            Pawn pawn1 = new BlackPawn();
            Pawn pawn2 = new BlackPawn();
            Pawn pawn3 = new BlackPawn();
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
            Pawn pawn = new WhitePawn();
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
            Pawn pawn1 = new WhitePawn();
            Pawn pawn2 = new BlackPawn();
            chessBoard.add(pawn1, e4);
            chessBoard.add(pawn2, g1);
            ChargeCard chargeCard = new ChargeCard();
chessBoard.setTurn(Color.WHITE);

            assertThrows(CannotMoveThisColorException.class, () -> chargeCard.playOn(chessBoard, new ChargeCardParam(Set.of(pawn1, pawn2))));

            assertEquals(pawn1, chessBoard.at(e4).getPiece().get());
            assertEquals(pawn2, chessBoard.at(g1).getPiece().get());
        }

        @Test
        void should_not_move_pawns_of_enemy_color_bis() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            Pawn pawn2 = new WhitePawn();
            chessBoard.add(pawn2, g8);
            ChargeCard chargeCard = new ChargeCard();
            chessBoard.setTurn(Color.BLACK);

            assertThrows(CannotMoveThisColorException.class, () -> chargeCard.playOn(chessBoard, new ChargeCardParam(Set.of(pawn2))));

            assertEquals(pawn2, chessBoard.at(g8).getPiece().get());
        }
    }
}