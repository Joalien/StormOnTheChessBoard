package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class PegasusCardIT {

    private ChessBoard chessBoard;
    private PegasusCard pegasusCard;

    @BeforeEach
    void setUp() {
        chessBoard = ChessBoard.createEmpty();
        chessBoard.add(new King(Color.WHITE), a1);
        chessBoard.add(new King(Color.BLACK), h8);
        chessBoard.setTurn(Color.WHITE);
        pegasusCard = new PegasusCard();
    }

    @Test
    void should_move_knight_to_opposite_color_square() {
        Knight knight = new Knight(Color.WHITE);
        chessBoard.add(knight, b1); // b1 isWhiteSquare=true

        pegasusCard.playOn(chessBoard, new PieceToPositionCardParam(knight, c3)); // c3 isWhiteSquare=false

        assertTrue(chessBoard.at(b1).getPiece().isEmpty());
        assertSame(knight, chessBoard.at(c3).getPiece().get());
    }

    @Test
    void should_not_move_knight_to_same_color_square() {
        Knight knight = new Knight(Color.WHITE);
        chessBoard.add(knight, b1); // b1 isWhiteSquare=true

        assertThrows(IllegalArgumentException.class,
                () -> pegasusCard.playOn(chessBoard, new PieceToPositionCardParam(knight, e4))); // e4 isWhiteSquare=true
    }

    @Test
    void should_not_move_to_occupied_square() {
        Knight knight = new Knight(Color.WHITE);
        chessBoard.add(knight, b1);
        chessBoard.add(new Pawn(Color.WHITE), c3); // c3 is opposite color from b1

        assertThrows(IllegalArgumentException.class,
                () -> pegasusCard.playOn(chessBoard, new PieceToPositionCardParam(knight, c3)));
    }

    @Test
    void should_not_move_non_knight_piece() {
        Pawn pawn = new Pawn(Color.WHITE);
        chessBoard.add(pawn, b2);

        assertThrows(IllegalArgumentException.class,
                () -> pegasusCard.playOn(chessBoard, new PieceToPositionCardParam(pawn, c4)));
    }

    @Test
    void should_not_move_opponent_knight() {
        Knight blackKnight = new Knight(Color.BLACK);
        chessBoard.add(blackKnight, b8);

        assertThrows(CannotMoveThisColorException.class,
                () -> pegasusCard.playOn(chessBoard, new PieceToPositionCardParam(blackKnight, c6)));
    }
}
