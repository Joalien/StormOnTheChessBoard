package fr.kubys.ai;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;
import fr.kubys.game.ChessBoardServiceFactory;
import fr.kubys.game.GameStateController;
import fr.kubys.piece.*;
import fr.kubys.piece.extra.Crab;
import fr.kubys.piece.extra.Kangaroo;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class FenConverterTest {

    @Test
    void should_convert_initial_position_to_standard_fen() {
        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(ChessBoard::createWithInitialState);
        gsc.startGame(1L);

        String fen = FenConverter.toFen(gsc);

        assertTrue(fen.startsWith("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"));
        assertTrue(fen.contains(" w "));
        assertTrue(fen.contains("KQkq"));
    }

    @Test
    void should_convert_custom_position() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Rock(Color.WHITE), a1);
        board.add(new Pawn(Color.WHITE), e2);
        board.setTurn(Color.WHITE);

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        String fen = FenConverter.toFen(gsc);

        assertTrue(fen.startsWith("4k3/8/8/8/8/8/4P3/R3K3"));
        assertTrue(fen.contains(" w "));
    }

    @Test
    void should_handle_black_turn() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.BLACK);

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        String fen = FenConverter.toFen(gsc);

        assertTrue(fen.contains(" b "));
    }

    @Test
    void should_include_castling_rights() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new Rock(Color.WHITE), h1);
        board.add(new Rock(Color.WHITE), a1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Rock(Color.BLACK), h8);
        board.setTurn(Color.WHITE);

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        String fen = FenConverter.toFen(gsc);

        assertTrue(fen.contains("KQk"), "Should have white KQ and black k castling: " + fen);
    }

    @Test
    void should_show_no_castling_after_king_moved() {
        ChessBoard board = ChessBoard.createEmpty();
        King whiteKing = new King(Color.WHITE);
        board.add(whiteKing, e1);
        board.add(new Rock(Color.WHITE), h1);
        board.add(new King(Color.BLACK), e8);
        board.setTurn(Color.WHITE);

        whiteKing.cannotCastleAnymore();

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        String fen = FenConverter.toFen(gsc);

        // Castling field is the 3rd space-separated token
        String castlingField = fen.split(" ")[2];
        assertEquals("-", castlingField, "No castling rights expected: " + fen);
    }

    @Test
    void kangaroo_is_approximated_as_knight() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Kangaroo(Color.WHITE), d4);
        board.setTurn(Color.WHITE);

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        // Kangaroo on d4 → 'N' (white knight) at d4 in the FEN
        assertTrue(FenConverter.toFen(gsc).startsWith("4k3/8/8/8/3N4/8/8/4K3"));
        // hasNonStandardPieces stays false because the approximation makes the FEN valid
        assertFalse(FenConverter.hasNonStandardPieces(gsc));
    }

    @Test
    void pieceToFenChar_maps_all_pieces_including_custom_approximations() {
        assertEquals('k', FenConverter.pieceToFenChar(new King(Color.WHITE)));
        assertEquals('q', FenConverter.pieceToFenChar(new Queen(Color.WHITE)));
        assertEquals('r', FenConverter.pieceToFenChar(new Rock(Color.WHITE)));
        assertEquals('b', FenConverter.pieceToFenChar(new Bishop(Color.WHITE)));
        assertEquals('n', FenConverter.pieceToFenChar(new Knight(Color.WHITE)));
        assertEquals('p', FenConverter.pieceToFenChar(new Pawn(Color.WHITE)));
        assertEquals('n', FenConverter.pieceToFenChar(new Kangaroo(Color.WHITE)));
        assertEquals('p', FenConverter.pieceToFenChar(new Crab(Color.WHITE)));
    }
}
