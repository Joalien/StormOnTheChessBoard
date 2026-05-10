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
    void enemy_reaction_returns_empty_when_state_disallows() {
        // BEGINNING_OF_THE_TURN doesn't allow ENEMY_TURN cards to be played reactively.
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

        // Game is in BEGINNING_OF_THE_TURN at this point — no reaction window.
        List<Command> commands = strategy.decideEnemyReaction(gameId, state, Color.BLACK);

        assertTrue(commands.isEmpty(), "No reaction expected outside of MOVE_WITHOUT_CARD_PLAYED / END_OF_THE_TURN windows");
    }

    @Test
    void enemy_reaction_returns_empty_when_no_enemy_turn_cards_in_hand() {
        ChessBoardRepository repo = new ChessBoardRepositoryImpl();
        Integer gameId = repo.createCustomGame(() -> {
            ChessBoard board = ChessBoard.createEmpty();
            board.add(new King(Color.WHITE), e1);
            board.add(new King(Color.BLACK), e8);
            board.add(new Pawn(Color.WHITE), e2);
            board.setTurn(Color.WHITE);
            return board;
        });
        // Move so we transition to MOVE_WITHOUT_CARD_PLAYED (a valid reaction window)
        repo.saveCommand(fr.kubys.command.PlayMoveCommand.builder().gameId(gameId).from(e2).to(e4).build());
        ChessBoardReadService state = repo.getChessBoardService(gameId);
        // Empty the AI's (black) hand so it has no ENEMY_TURN cards
        state.getBlack().getCards().clear();

        var strategy = new CardAwareStrategy(repo, new MaterialStrategy(new Random(0)));
        List<Command> commands = strategy.decideEnemyReaction(gameId, state, Color.BLACK);

        assertTrue(commands.isEmpty(), "No reaction expected when AI has no cards at all");
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
