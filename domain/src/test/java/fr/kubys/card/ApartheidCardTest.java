package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.NoCardParam;
import fr.kubys.card.params.PositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ApartheidCardTest {

    private ChessBoard chessBoard;
    private ApartheidCard apartheidCard;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
        apartheidCard = new ApartheidCard();
        chessBoard.setTurn(Color.WHITE);
    }

    @Nested
    class Success {
        @Test
        void should_remove_white_pawn_on_black_square() {
            chessBoard = ChessBoard.createWithInitialState();
            apartheidCard.playOn(chessBoard, new NoCardParam());

            assertTrue(Set.of(a2, c2, e2, g2).stream()
                .map(p -> chessBoard.at(p))
                .map(Square::getPiece)
                .allMatch(Optional::isPresent));
            assertTrue(Set.of(b2, d2, f2, h2).stream()
                .map(p -> chessBoard.at(p))
                .map(Square::getPiece)
                .allMatch(Optional::isEmpty));
        }

        @Test
        void should_remove_black_pawn_on_white_square() {
            chessBoard = ChessBoard.createWithInitialState();
            apartheidCard.playOn(chessBoard, new NoCardParam());

            assertTrue(Set.of(a7, c7, e7, g7).stream()
                .map(p -> chessBoard.at(p))
                .map(Square::getPiece)
                .allMatch(Optional::isPresent));
            assertTrue(Set.of(b7, d7, f7, h7).stream()
                .map(p -> chessBoard.at(p))
                .map(Square::getPiece)
                .allMatch(Optional::isEmpty));
        }

        @Test
        void should_throw_if_removing_pawn_checks_our_king() {
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, e4);
            King king = new King(Color.WHITE);
            chessBoard.add(king, e1);
            WhitePawn pawn = new WhitePawn();
            chessBoard.add(pawn, e2); // white pawn on white square

            assertDoesNotThrow(() -> apartheidCard.playOn(chessBoard, new NoCardParam()));
        }
    }

    @Nested
    class Failure {
        @Test
        void should_throw_if_removing_pawn_checks_our_king() {
            Queen queen = new Queen(Color.BLACK);
            chessBoard.add(queen, e4);
            King king = new King(Color.WHITE);
            chessBoard.add(king, e1);
            WhitePawn pawn = new WhitePawn();
            chessBoard.add(pawn, e3); // white pawn on black square

            assertThrows(CheckException.class, () -> apartheidCard.playOn(chessBoard, new NoCardParam()));
        }
    }
}