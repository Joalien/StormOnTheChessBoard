package fr.kubys.ai.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;
import fr.kubys.game.ChessBoardServiceFactory;
import fr.kubys.game.GameStateController;
import fr.kubys.piece.King;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Queen;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardEvaluatorTest {

    private static GameStateController controller(ChessBoard board) {
        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);
        return gsc;
    }

    @Test
    void mirrored_material_evaluates_to_zero() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Queen(Color.WHITE), d1);
        board.add(new Queen(Color.BLACK), d8);
        board.setTurn(Color.WHITE);
        var gsc = controller(board);

        assertEquals(0, BoardEvaluator.evaluate(gsc, Color.WHITE));
        assertEquals(0, BoardEvaluator.evaluate(gsc, Color.BLACK));
    }

    @Test
    void extra_pawn_for_white_is_positive_for_white() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Pawn(Color.WHITE), e2);
        board.setTurn(Color.WHITE);
        var gsc = controller(board);

        assertEquals(10, BoardEvaluator.evaluate(gsc, Color.WHITE));
        assertEquals(-10, BoardEvaluator.evaluate(gsc, Color.BLACK));
    }

    @Test
    void capturing_a_rook_swings_eval_by_fifty() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Rock(Color.BLACK), a8);
        board.setTurn(Color.WHITE);
        var gsc = controller(board);

        assertEquals(-50, BoardEvaluator.evaluate(gsc, Color.WHITE));
    }

    @Test
    void checkmate_returns_winning_score_from_winner_perspective() {
        // Back-rank mate: black king at h8 trapped behind own pawns, white rook gives check on rank 8.
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.BLACK), h8);
        board.add(new Pawn(Color.BLACK), g7);
        board.add(new Pawn(Color.BLACK), h7);
        board.add(new King(Color.WHITE), e1);
        board.add(new Rock(Color.WHITE), a8);
        board.setTurn(Color.BLACK);
        var gsc = controller(board);

        assertEquals(BoardEvaluator.CHECKMATE_SCORE, BoardEvaluator.evaluate(gsc, Color.WHITE));
        assertEquals(-BoardEvaluator.CHECKMATE_SCORE, BoardEvaluator.evaluate(gsc, Color.BLACK));
    }

    @Test
    void check_against_opponent_adds_bonus() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Rock(Color.WHITE), e7);
        board.setTurn(Color.BLACK);
        var gsc = controller(board);

        // From white's perspective: +rook (50) +check bonus (20) = 70
        assertTrue(BoardEvaluator.evaluate(gsc, Color.WHITE) > 50);
    }
}
