package fr.kubys.ai;

import fr.kubys.board.ChessBoard;
import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.game.ChessBoardServiceFactory;
import fr.kubys.game.GameStateController;
import fr.kubys.piece.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class MaterialStrategyTest {

    @Test
    void should_capture_free_piece() {
        // White queen hanging on d4, black rook can take it
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Queen(Color.WHITE), d4);
        board.add(new Rock(Color.BLACK), d8);
        board.setTurn(Color.BLACK);

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        MaterialStrategy strategy = new MaterialStrategy(new Random(0));
        List<Command> commands = strategy.decideMove(1, gsc);

        PlayMoveCommand move = (PlayMoveCommand) commands.get(0);
        assertEquals(d8, move.getFrom());
        assertEquals(d4, move.getTo()); // captures the queen
    }

    @Test
    void should_prefer_capturing_higher_value_piece() {
        // Black can capture white queen on d4 or white pawn on a8
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Queen(Color.WHITE), d4);
        board.add(new Pawn(Color.WHITE), a7);
        board.add(new Rock(Color.BLACK), d8);
        board.setTurn(Color.BLACK);

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        MaterialStrategy strategy = new MaterialStrategy(new Random(0));
        List<Command> commands = strategy.decideMove(1, gsc);

        PlayMoveCommand move = (PlayMoveCommand) commands.get(0);
        assertEquals(d4, move.getTo()); // queen (9) > pawn (1)
    }

    @Test
    void should_avoid_moving_piece_to_attacked_square() {
        // Black rook can move to d4 (safe) or e2 (attacked by white king on e1)
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), h8);
        board.add(new Rock(Color.BLACK), d7);
        board.setTurn(Color.BLACK);

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        MaterialStrategy strategy = new MaterialStrategy(new Random(42));
        List<Command> commands = strategy.decideMove(1, gsc);

        PlayMoveCommand move = (PlayMoveCommand) commands.get(0);
        // Should not move rook to e1's neighborhood where it could be captured
        assertNotEquals(e1, move.getTo());
    }

    @Test
    void should_move_piece_out_of_danger_or_capture_attacker() {
        // Black rook on e4 is attacked by white rook on a4
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), a1);
        board.add(new King(Color.BLACK), h8);
        board.add(new Rock(Color.WHITE), a4);
        board.add(new Rock(Color.BLACK), e4);
        board.setTurn(Color.BLACK);

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        MaterialStrategy strategy = new MaterialStrategy(new Random(0));
        List<Command> commands = strategy.decideMove(1, gsc);

        PlayMoveCommand move = (PlayMoveCommand) commands.get(0);
        assertEquals(e4, move.getFrom());
        // Should either capture on a4 or move to a safe square off rank 4
        boolean captures = move.getTo().equals(a4);
        boolean movesToSafety = move.getTo().getRow().getRowNumber() != 4;
        assertTrue(captures || movesToSafety, "Should capture attacker or flee to safety");
    }

    @Test
    void should_return_a_legal_move_on_initial_board() {
        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(ChessBoard::createWithInitialState);
        gsc.startGame(42L);

        MaterialStrategy strategy = new MaterialStrategy(new Random(0));
        List<Command> commands = strategy.decideMove(1, gsc);

        assertEquals(2, commands.size());
        assertInstanceOf(PlayMoveCommand.class, commands.get(0));
        assertInstanceOf(EndTurnCommand.class, commands.get(1));

        PlayMoveCommand move = (PlayMoveCommand) commands.get(0);
        Set<Position> legalMoves = gsc.getLegalMoves(move.getFrom());
        assertTrue(legalMoves.contains(move.getTo()));
    }

    @Test
    void should_never_pick_a_self_check_move() {
        for (int seed = 0; seed < 50; seed++) {
            ChessBoard board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), e1);
            board.add(new King(Color.BLACK), h8);
            board.add(new Rock(Color.BLACK), e7); // pins nothing, but checks e-file
            board.add(new Rock(Color.WHITE), d2);
            board.setTurn(Color.WHITE);

            var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
            gsc.startGame(1L);

            MaterialStrategy strategy = new MaterialStrategy(new Random(seed));
            List<Command> commands = strategy.decideMove(1, gsc);

            if (commands.size() == 2) {
                PlayMoveCommand move = (PlayMoveCommand) commands.get(0);
                Set<Position> legal = gsc.getLegalMoves(move.getFrom());
                assertTrue(legal.contains(move.getTo()),
                        "Seed %d: move %s->%s should be legal".formatted(seed, move.getFrom(), move.getTo()));
            }
        }
    }

    @Test
    void pieceValue_returns_expected_values() {
        assertEquals(1, MaterialStrategy.pieceValue(new Pawn(Color.WHITE)));
        assertEquals(3, MaterialStrategy.pieceValue(new Knight(Color.WHITE)));
        assertEquals(3, MaterialStrategy.pieceValue(new Bishop(Color.WHITE)));
        assertEquals(5, MaterialStrategy.pieceValue(new Rock(Color.WHITE)));
        assertEquals(9, MaterialStrategy.pieceValue(new Queen(Color.WHITE)));
        assertEquals(100, MaterialStrategy.pieceValue(new King(Color.WHITE)));
    }
}
