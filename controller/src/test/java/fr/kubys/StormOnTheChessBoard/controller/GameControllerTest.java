package fr.kubys.StormOnTheChessBoard.controller;

import core.Color;
import fr.kubys.StormOnTheChessBoard.controller.GameController;
import org.junit.jupiter.api.Test;
import piece.BlackPawn;
import piece.King;
import piece.Knight;
import piece.Piece;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameControllerTest {

    @Test
    void should_convert_piece_to_pieceDto() {
        Piece whiteKing = new King(Color.WHITE);

        assertEquals("wK", GameController.map(whiteKing));
    }

    @Test
    void should_convert_piece_to_pieceDto_bis() {
        Piece blackKnight = new Knight(Color.BLACK);

        assertEquals("bN", GameController.map(blackKnight));
    }

    @Test
    void should_convert_piece_to_pieceDto_ter() {
        Piece blackPawn = new BlackPawn();

        assertEquals("bP", GameController.map(blackPawn));
    }
}