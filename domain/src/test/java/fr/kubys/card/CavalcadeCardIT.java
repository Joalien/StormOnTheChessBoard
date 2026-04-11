package fr.kubys.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.card.params.PieceToPositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Queen;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CavalcadeCardIT {

    @Test
    void should_promote_white_pawn_to_queen_when_cavalcade_reaches_row_8() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        chessBoard.add(new King(Color.WHITE), a1);
        chessBoard.add(new King(Color.BLACK), h8);
        Pawn pawn = new Pawn(Color.WHITE);
        chessBoard.add(pawn, e6); // e6 -> f8 : valid knight move (1 file, 2 rows)
        chessBoard.setTurn(Color.WHITE);

        new CavalcadeCard().playOn(chessBoard, new PieceToPositionCardParam(pawn, f8));

        assertInstanceOf(Queen.class, chessBoard.at(f8).getPiece().get());
        assertEquals(Color.WHITE, chessBoard.at(f8).getPiece().get().getColor());
        assertTrue(chessBoard.at(e6).getPiece().isEmpty());
    }

    @Test
    void should_promote_black_pawn_to_queen_when_cavalcade_reaches_row_1() {
        ChessBoard chessBoard = ChessBoard.createEmpty();
        chessBoard.add(new King(Color.WHITE), h1);
        chessBoard.add(new King(Color.BLACK), a8);
        Pawn pawn = new Pawn(Color.BLACK);
        chessBoard.add(pawn, e3); // e3 -> f1 : valid knight move (1 file, 2 rows)
        chessBoard.setTurn(Color.BLACK);

        new CavalcadeCard().playOn(chessBoard, new PieceToPositionCardParam(pawn, f1));

        assertInstanceOf(Queen.class, chessBoard.at(f1).getPiece().get());
        assertEquals(Color.BLACK, chessBoard.at(f1).getPiece().get().getColor());
        assertTrue(chessBoard.at(e3).getPiece().isEmpty());
    }
}
