package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceCardParam;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Piece;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MagnetismCardTest {

    private final Position c2 = Position.c2;
    private ChessBoard chessBoard;
    private MagnetismCard magnetismCard;
    private Piece piece;


    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createWithInitialState();
        piece = chessBoard.at(c2).getPiece().get();
        magnetismCard = new MagnetismCard();
        magnetismCard.setIsPlayedBy(Color.WHITE);
    }

    @Nested
    class Success {
        @Test
        void should_add_effect_magnetism_on_pawn_c2() {
            assertDoesNotThrow(() -> magnetismCard.playOn(chessBoard, new PieceCardParam(piece)));
            assertEquals(1, chessBoard.getEffects().size());
        }
    }

    @Nested
    class Failure {
        @Test
        void should_throws_if_piece_not_on_the_board() {
            assertThrows(IllegalArgumentException.class, () -> magnetismCard.playOn(chessBoard, new PieceCardParam(new Queen(Color.WHITE))));
        }

        @Test
        void should_throw_if_cast_on_enemy_piece() {
            magnetismCard.setIsPlayedBy(Color.BLACK);
            assertThrows(CannotMoveThisColorException.class, () -> magnetismCard.playOn(chessBoard, new PieceCardParam(piece)));
        }
    }
}