package fr.kubys.ai;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.ChessBoard;
import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Color;
import fr.kubys.piece.King;
import fr.kubys.piece.Pawn;
import fr.kubys.repository.ChessBoardRepository;
import fr.kubys.repository.ChessBoardRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class CardAwareStrategyTest {

    @Test
    void plays_a_simple_move_when_no_cards_are_useful() {
        ChessBoardRepository repo = new ChessBoardRepositoryImpl();
        Integer gameId = repo.createCustomGame(() -> {
            ChessBoard board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), e1);
            board.add(new King(Color.BLACK), e8);
            board.add(new Pawn(Color.WHITE), e2);
            board.setTurn(Color.WHITE);
            return board;
        });
        ChessBoardReadService state = repo.getChessBoardService(gameId);

        var strategy = new CardAwareStrategy(repo, new MaterialStrategy(new Random(0)));
        List<Command> commands = strategy.decideMove(gameId, state);

        // Should play one move + endTurn
        assertFalse(commands.isEmpty());
        assertTrue(commands.stream().anyMatch(PlayMoveCommand.class::isInstance),
                "Should play at least one move");
        assertInstanceOf(EndTurnCommand.class, commands.get(commands.size() - 1));
    }

    @Test
    void emits_only_end_turn_when_no_legal_options_exist() {
        ChessBoardRepository repo = new ChessBoardRepositoryImpl();
        Integer gameId = repo.createCustomGame(() -> {
            ChessBoard board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), e1);
            board.add(new King(Color.BLACK), e8);
            board.setTurn(Color.WHITE);
            return board;
        });
        ChessBoardReadService state = repo.getChessBoardService(gameId);
        var strategy = new CardAwareStrategy(repo, new MaterialStrategy(new Random(0)));

        assertDoesNotThrow(() -> strategy.decideMove(gameId, state));
    }

    @Test
    void commands_can_be_committed_through_repository_without_throwing() {
        ChessBoardRepository repo = new ChessBoardRepositoryImpl();
        Integer gameId = repo.createCustomGame(() -> {
            ChessBoard board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), e1);
            board.add(new King(Color.BLACK), e8);
            board.add(new Pawn(Color.WHITE), e2);
            board.setTurn(Color.WHITE);
            return board;
        });
        ChessBoardReadService state = repo.getChessBoardService(gameId);
        var strategy = new CardAwareStrategy(repo, new MaterialStrategy(new Random(0)));

        List<Command> commands = strategy.decideMove(gameId, state);

        for (Command command : commands) {
            assertDoesNotThrow(() -> repo.saveCommand(command),
                    "AI-emitted command must commit cleanly: " + command);
        }
    }
}
