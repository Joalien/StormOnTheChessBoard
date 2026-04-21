package fr.kubys.mapper;

import fr.kubys.card.DoubleStrikeCard;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputMapperTest {

    @Test
    void should_convert_piece_to_pieceDto() {
        Piece whiteKing = new King(Color.WHITE);

        assertEquals("wK", OutputMapper.map(whiteKing));
    }

    @Test
    void should_convert_piece_to_pieceDto_2() {
        Piece whiteKing = new Pawn(Color.WHITE);

        assertEquals("wP", OutputMapper.map(whiteKing));
    }

    @Test
    void should_convert_piece_to_pieceDto_3() {
        Piece blackKnight = new Knight(Color.BLACK);

        assertEquals("bN", OutputMapper.map(blackKnight));
    }

    @Test
    void should_convert_piece_to_pieceDto_4() {
        Piece blackPawn = new Pawn(Color.BLACK);

        assertEquals("bP", OutputMapper.map(blackPawn));
    }

    @Test
    void card_param_keys_must_preserve_field_declaration_order() {
        var dto = OutputMapper.map(new DoubleStrikeCard());

        assertEquals(List.of("piece1", "position1", "piece2", "position2"),
                List.copyOf(dto.getParam().keySet()),
                "Param order drives the UI display — each (piece, destination) pair must stay grouped");
    }
}