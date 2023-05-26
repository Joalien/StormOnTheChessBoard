package fr.kubys.controller;

import fr.kubys.mapper.ModelMapper;
import fr.kubys.core.Color;
import fr.kubys.piece.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelMapperTest {

    @Test
    void should_convert_piece_to_pieceDto() {
        Piece whiteKing = new King(Color.WHITE);

        assertEquals("wK", ModelMapper.map(whiteKing));
    }

    @Test
    void should_convert_piece_to_pieceDto_2() {
        Piece whiteKing = new WhitePawn();

        assertEquals("wP", ModelMapper.map(whiteKing));
    }

    @Test
    void should_convert_piece_to_pieceDto_3() {
        Piece blackKnight = new Knight(Color.BLACK);

        assertEquals("bN", ModelMapper.map(blackKnight));
    }

    @Test
    void should_convert_piece_to_pieceDto_4() {
        Piece blackPawn = new BlackPawn();

        assertEquals("bP", ModelMapper.map(blackPawn));
    }
}