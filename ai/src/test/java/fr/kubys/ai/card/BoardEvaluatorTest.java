package fr.kubys.ai.card;

import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;
import fr.kubys.game.ChessBoardServiceFactory;
import fr.kubys.game.GameStateController;
import fr.kubys.piece.King;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Queen;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class BoardEvaluatorTest {

    private static GameStateController controller(ChessBoard board) {
        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);
        return gsc;
    }

    @Test
    void mirrored_position_evaluates_to_zero() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Queen(Color.WHITE), d1);
        board.add(new Queen(Color.BLACK), d8);
        board.setTurn(Color.WHITE);
        var gsc = controller(board);

        // Symmetric position: any positional bonus cancels between sides.
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

        // Material dominates: a pawn is worth 10. Mobility from the extra pawn adds a few
        // points but never approaches the pawn's value.
        int whiteScore = BoardEvaluator.evaluate(gsc, Color.WHITE);
        assertTrue(whiteScore > 5 && whiteScore < 20,
                "White with extra pawn should score around +10, got " + whiteScore);
        assertEquals(-whiteScore, BoardEvaluator.evaluate(gsc, Color.BLACK));
    }

    @Test
    void losing_a_rook_dominates_positional_terms() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Rock(Color.BLACK), a8);
        board.setTurn(Color.WHITE);
        var gsc = controller(board);

        int whiteScore = BoardEvaluator.evaluate(gsc, Color.WHITE);
        // Black has a rook (-50 material). Positional terms shouldn't override that.
        assertTrue(whiteScore < -40 && whiteScore > -80,
                "Down a rook should be around -50, got " + whiteScore);
    }

    @Test
    void checkmate_returns_winning_score_from_winner_perspective() {
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

        // From white's perspective: +rook (50) +check bonus (20) = 70 plus positional terms.
        assertTrue(BoardEvaluator.evaluate(gsc, Color.WHITE) > 50);
    }

    @Test
    void developing_a_knight_off_back_rank_is_positive() {
        ChessBoard backRank = ChessBoard.createEmpty();
        backRank.add(new King(Color.WHITE), e1);
        backRank.add(new King(Color.BLACK), e8);
        backRank.add(new Knight(Color.WHITE), b1);
        backRank.setTurn(Color.WHITE);
        var before = controller(backRank);
        int beforeScore = BoardEvaluator.evaluate(before, Color.WHITE);

        ChessBoard developed = ChessBoard.createEmpty();
        developed.add(new King(Color.WHITE), e1);
        developed.add(new King(Color.BLACK), e8);
        developed.add(new Knight(Color.WHITE), c3);
        developed.setTurn(Color.WHITE);
        var after = controller(developed);
        int afterScore = BoardEvaluator.evaluate(after, Color.WHITE);

        assertTrue(afterScore > beforeScore,
                "Developing knight from b1 to c3 should improve eval (before=%d after=%d)".formatted(beforeScore, afterScore));
    }

    @Test
    void advancing_a_central_pawn_is_positive() {
        ChessBoard backRank = ChessBoard.createEmpty();
        backRank.add(new King(Color.WHITE), e1);
        backRank.add(new King(Color.BLACK), e8);
        backRank.add(new Pawn(Color.WHITE), e2);
        backRank.setTurn(Color.WHITE);
        var before = controller(backRank);
        int beforeScore = BoardEvaluator.evaluate(before, Color.WHITE);

        ChessBoard advanced = ChessBoard.createEmpty();
        advanced.add(new King(Color.WHITE), e1);
        advanced.add(new King(Color.BLACK), e8);
        advanced.add(new Pawn(Color.WHITE), e4);
        advanced.setTurn(Color.WHITE);
        var after = controller(advanced);
        int afterScore = BoardEvaluator.evaluate(after, Color.WHITE);

        assertTrue(afterScore > beforeScore,
                "Pushing a pawn to e4 should improve eval (before=%d after=%d)".formatted(beforeScore, afterScore));
    }

    @Test
    void controlling_center_squares_is_positive() {
        ChessBoard offCenter = ChessBoard.createEmpty();
        offCenter.add(new King(Color.WHITE), e1);
        offCenter.add(new King(Color.BLACK), e8);
        offCenter.add(new Knight(Color.WHITE), b1);
        offCenter.setTurn(Color.WHITE);
        var before = controller(offCenter);
        int beforeScore = BoardEvaluator.evaluate(before, Color.WHITE);

        ChessBoard inCenter = ChessBoard.createEmpty();
        inCenter.add(new King(Color.WHITE), e1);
        inCenter.add(new King(Color.BLACK), e8);
        inCenter.add(new Knight(Color.WHITE), e4);
        inCenter.setTurn(Color.WHITE);
        var after = controller(inCenter);
        int afterScore = BoardEvaluator.evaluate(after, Color.WHITE);

        assertTrue(afterScore > beforeScore,
                "Knight on e4 should beat knight on b1 (before=%d after=%d)".formatted(beforeScore, afterScore));
    }
}
