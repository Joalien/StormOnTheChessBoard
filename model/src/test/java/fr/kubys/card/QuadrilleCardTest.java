package fr.kubys.card;

import fr.kubys.board.CheckException;
import fr.kubys.board.ChessBoard;
import fr.kubys.card.Card;
import fr.kubys.card.QuadrilleCard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Rock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static fr.kubys.core.Position.*;

class QuadrilleCardTest {

    private ChessBoard chessBoard;
    private Rock a1Rock;
    private Rock h1Rock;
    private Rock a8Rock;
    private Rock h8Rock;
    private Card clockwiseQuadrille;
    private Card counterclockwiseQuadrille;


    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createWithInitialState();
        a1Rock = (Rock) chessBoard.at(a1).getPiece().get();
        h1Rock = (Rock) chessBoard.at(h1).getPiece().get();
        a8Rock = (Rock) chessBoard.at(a8).getPiece().get();
        h8Rock = (Rock) chessBoard.at(h8).getPiece().get();
        clockwiseQuadrille = new QuadrilleCard();
        clockwiseQuadrille.setIsPlayedBy(Color.WHITE);
        counterclockwiseQuadrille = new QuadrilleCard();
        counterclockwiseQuadrille.setIsPlayedBy(Color.WHITE);
    }

    @Nested
    class Success {
        @Test
        void should_turn_clockwise() {
            assertTrue(clockwiseQuadrille.playOn(chessBoard, List.of(QuadrilleCard.Direction.CLOCKWISE)));

            assertEquals(a1Rock, chessBoard.at(a8).getPiece().get());
            assertEquals(h1Rock, chessBoard.at(a1).getPiece().get());
            assertEquals(h8Rock, chessBoard.at(h1).getPiece().get());
            assertEquals(a8Rock, chessBoard.at(h8).getPiece().get());
        }

        @Test
        void should_turn_clockwise_with_empty_square() {
            chessBoard = ChessBoard.createEmpty();
            chessBoard.add(a1Rock, a1);
            chessBoard.add(h8Rock, h8);

            assertTrue(clockwiseQuadrille.playOn(chessBoard, List.of(QuadrilleCard.Direction.CLOCKWISE)));

            assertEquals(a1Rock, chessBoard.at(a8).getPiece().get());
            assertEquals(h8Rock, chessBoard.at(h1).getPiece().get());
            assertTrue(chessBoard.at(a1).getPiece().isEmpty());
            assertTrue(chessBoard.at(h8).getPiece().isEmpty());
        }

        @Test
        void should_turn_counterclockwise() {
            assertTrue(counterclockwiseQuadrille.playOn(chessBoard, List.of(QuadrilleCard.Direction.COUNTERCLOCKWISE)));

            assertEquals(a1Rock, chessBoard.at(h1).getPiece().get());
            assertEquals(h1Rock, chessBoard.at(h8).getPiece().get());
            assertEquals(h8Rock, chessBoard.at(a8).getPiece().get());
            assertEquals(a8Rock, chessBoard.at(a1).getPiece().get());
        }

        @Test
        void should_work_on_empty_chessboard() {
            chessBoard = ChessBoard.createEmpty();
            assertTrue(clockwiseQuadrille.playOn(chessBoard, List.of(QuadrilleCard.Direction.CLOCKWISE)));

            assertTrue(chessBoard.at(h1).getPiece().isEmpty());
            assertTrue(chessBoard.at(h8).getPiece().isEmpty());
            assertTrue(chessBoard.at(a8).getPiece().isEmpty());
            assertTrue(chessBoard.at(a1).getPiece().isEmpty());
        }
    }

    @Nested
    class Failure {
        @Test
        void should_not_turn_clockwise_if_it_creates_check() {
            chessBoard = ChessBoard.createEmpty();
            chessBoard.add(new King(Color.WHITE), e1);
            chessBoard.add(a8Rock, h8);

            Assertions.assertThrows(CheckException.class, () -> clockwiseQuadrille.playOn(chessBoard, List.of(QuadrilleCard.Direction.CLOCKWISE)));
            assertTrue(counterclockwiseQuadrille.playOn(chessBoard, List.of(QuadrilleCard.Direction.COUNTERCLOCKWISE)));
        }

        @Test
        void should_not_turn_counterclockwise_if_it_creates_check() {
            chessBoard = ChessBoard.createEmpty();
            chessBoard.add(new King(Color.WHITE), e1);
            chessBoard.add(a8Rock, a8);

            Assertions.assertThrows(CheckException.class, () -> counterclockwiseQuadrille.playOn(chessBoard, List.of(QuadrilleCard.Direction.COUNTERCLOCKWISE)));
            assertTrue(clockwiseQuadrille.playOn(chessBoard, List.of(QuadrilleCard.Direction.CLOCKWISE)));
        }
    }
}