package fr.kubys.game;

import fr.kubys.api.GameResult;
import fr.kubys.board.ChessBoard;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Queen;
import fr.kubys.piece.Rock;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameStateControllerGameResultTest {

    @Test
    void ongoing_from_initial_position() {
        GameStateController controller = new GameStateController();
        controller.startGame(42);

        assertEquals(GameResult.ONGOING, controller.getGameResult());
    }

    @Test
    void returns_black_wins_when_white_is_checkmated_on_its_turn() {
        // Back-rank mate against white: king on h1 blocked by its own pawns on
        // f2/g2/h2, black rook on a1 attacking the first rank. White to move has
        // no legal response.
        GameStateController controller = new GameStateController(() -> {
            ChessBoard board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), h1);
            board.add(new King(Color.BLACK), e8);
            board.add(new Pawn(Color.WHITE), f2);
            board.add(new Pawn(Color.WHITE), g2);
            board.add(new Pawn(Color.WHITE), h2);
            board.add(new Rock(Color.BLACK), a1);
            return board;
        });
        controller.startGame(42);

        assertEquals(GameResult.BLACK_WINS, controller.getGameResult());
    }

    @Test
    void returns_draw_on_stalemate() {
        // Classic stalemate: white king on a1, black queen on b3, black king on c2.
        // White to move. The king is not in check (b3 does not attack a1), but every
        // adjacent square (a2, b1, b2) is covered by the queen, so there is no legal
        // move.
        GameStateController controller = new GameStateController(() -> {
            ChessBoard board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), a1);
            board.add(new Queen(Color.BLACK), b3);
            board.add(new King(Color.BLACK), c2);
            return board;
        });
        controller.startGame(42);

        assertEquals(GameResult.DRAW, controller.getGameResult());
    }
}
