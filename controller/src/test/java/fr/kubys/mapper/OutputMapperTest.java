package fr.kubys.mapper;

import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputMapperTest {

    @Test
    void should_convert_piece_to_pieceDto() {
        Piece whiteKing = new King(Color.WHITE);

        assertEquals("wK", OutputMapper.map(whiteKing));
    }

    @Test
    void should_convert_piece_to_pieceDto_2() {
        Piece whiteKing = new WhitePawn();

        assertEquals("wP", OutputMapper.map(whiteKing));
    }

    @Test
    void should_convert_piece_to_pieceDto_3() {
        Piece blackKnight = new Knight(Color.BLACK);

        assertEquals("bN", OutputMapper.map(blackKnight));
    }

    @Test
    void should_convert_piece_to_pieceDto_4() {
        Piece blackPawn = new BlackPawn();

        assertEquals("bP", OutputMapper.map(blackPawn));
    }
}