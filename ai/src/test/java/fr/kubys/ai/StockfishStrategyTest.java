package fr.kubys.ai;

import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.BarricadeEffect;
import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.game.ChessBoardServiceFactory;
import fr.kubys.game.GameStateController;
import fr.kubys.piece.*;
import fr.kubys.piece.extra.Kangaroo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class StockfishStrategyTest {

    private static final String STOCKFISH_PATH = System.getProperty("stockfish.path", "src/main/resources/bin/fairy-stockfish");

    static boolean stockfishAvailable() {
        File f = new File(STOCKFISH_PATH);
        return f.exists() && f.canExecute();
    }

    @Test
    void should_fallback_when_effects_are_present() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Pawn(Color.WHITE), e2);
        board.addEffect(new BarricadeEffect(a3, a4, b3, b4));
        board.setTurn(Color.WHITE);

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        MaterialStrategy fallback = new MaterialStrategy(new Random(0));
        StockfishStrategy strategy = new StockfishStrategy("/nonexistent/path", fallback);

        List<Command> commands = strategy.decideMove(1, gsc);

        assertFalse(commands.isEmpty());
        assertInstanceOf(EndTurnCommand.class, commands.get(commands.size() - 1));
    }

    @Test
    void should_fallback_when_non_standard_pieces_present() {
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Kangaroo(Color.WHITE), d4);
        board.setTurn(Color.WHITE);

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        MaterialStrategy fallback = new MaterialStrategy(new Random(0));
        StockfishStrategy strategy = new StockfishStrategy("/nonexistent/path", fallback);

        List<Command> commands = strategy.decideMove(1, gsc);

        assertFalse(commands.isEmpty());
    }

    @Test
    void should_fallback_when_binary_not_found() {
        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(ChessBoard::createWithInitialState);
        gsc.startGame(1L);

        MaterialStrategy fallback = new MaterialStrategy(new Random(42));
        StockfishStrategy strategy = new StockfishStrategy("/nonexistent/fairy-stockfish", fallback);

        List<Command> commands = strategy.decideMove(1, gsc);

        assertEquals(2, commands.size());
        assertInstanceOf(PlayMoveCommand.class, commands.get(0));
        assertInstanceOf(EndTurnCommand.class, commands.get(1));
    }

    @Test
    @EnabledIf("stockfishAvailable")
    void should_return_legal_move_from_stockfish() {
        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(ChessBoard::createWithInitialState);
        gsc.startGame(1L);

        MaterialStrategy fallback = new MaterialStrategy(new Random(0));
        StockfishStrategy strategy = new StockfishStrategy(STOCKFISH_PATH, fallback);

        List<Command> commands = strategy.decideMove(1, gsc);

        assertEquals(2, commands.size());
        PlayMoveCommand move = (PlayMoveCommand) commands.get(0);
        assertInstanceOf(EndTurnCommand.class, commands.get(1));

        Set<Position> legalMoves = gsc.getLegalMoves(move.getFrom());
        assertTrue(legalMoves.contains(move.getTo()),
                "Stockfish move %s→%s should be legal".formatted(move.getFrom(), move.getTo()));
    }

    @Test
    @EnabledIf("stockfishAvailable")
    void should_find_obvious_capture() {
        // Black queen hanging on d4, white rook on d1 can take
        ChessBoard board = ChessBoard.createEmpty();
        board.add(new King(Color.WHITE), e1);
        board.add(new King(Color.BLACK), e8);
        board.add(new Queen(Color.BLACK), d4);
        board.add(new Rock(Color.WHITE), d1);
        board.setTurn(Color.WHITE);

        var gsc = (GameStateController) ChessBoardServiceFactory.newChessBoardService(() -> board);
        gsc.startGame(1L);

        StockfishStrategy strategy = new StockfishStrategy(STOCKFISH_PATH, new MaterialStrategy());
        List<Command> commands = strategy.decideMove(1, gsc);

        PlayMoveCommand move = (PlayMoveCommand) commands.get(0);
        assertEquals(d1, move.getFrom());
        assertEquals(d4, move.getTo());
    }
}
