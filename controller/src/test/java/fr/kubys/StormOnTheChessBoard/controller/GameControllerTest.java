package fr.kubys.StormOnTheChessBoard.controller;

import fr.kubys.core.Color;
import org.junit.jupiter.api.Test;
import fr.kubys.piece.BlackPawn;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Piece;

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