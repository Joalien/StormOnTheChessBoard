package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.CardParam;
import fr.kubys.card.params.PositionCardParam;
import fr.kubys.card.params.QuadrilleCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.Queen;
import fr.kubys.piece.extra.BlackHole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.e4;
import static org.junit.jupiter.api.Assertions.*;

class BlackHoleCardTest {

    @Nested
    class Success {
        @Test
        void should_not_be_able_to_add_piece_on_black_hole() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            BlackHoleCard blackHoleCard = new BlackHoleCard();

            assertDoesNotThrow(() -> blackHoleCard.playOn(chessBoard, new PositionCardParam(e4)));

            assertTrue(chessBoard.at(e4).getPiece().get() instanceof BlackHole);
            assertThrows(IllegalArgumentException.class, () -> chessBoard.add(new Queen(Color.WHITE), e4));
        }
    }

    @Nested
    class Failure {
        @Test
        void should_throw_if_square_is_not_empty() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            chessBoard.add(new Queen(Color.WHITE), e4);
            BlackHoleCard blackHoleCard = new BlackHoleCard();

            assertThrows(IllegalArgumentException.class, () -> blackHoleCard.playOn(chessBoard, new PositionCardParam(e4)));
        }

        @Test
        void should_throw_if_square_already_have_black_hole() {
            ChessBoard chessBoard = ChessBoard.createEmpty();
            BlackHoleCard blackHoleCard = new BlackHoleCard();

            assertDoesNotThrow(() -> blackHoleCard.playOn(chessBoard, new PositionCardParam(e4)));
            assertThrows(IllegalArgumentException.class, () -> blackHoleCard.playOn(chessBoard, new PositionCardParam(e4)));
        }
    }
}