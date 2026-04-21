package fr.kubys.ai;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.player.Player;
import fr.kubys.repository.ChessBoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiGameServiceTest {

    @Mock
    ChessBoardRepository chessBoardRepository;

    @Mock
    ChessBoardReadService boardState;

    AiGameService aiGameService;

    @BeforeEach
    void setUp() {
        // Synchronous executor: schedulePlayIfAiTurn runs in-caller thread, deterministic.
        aiGameService = new AiGameService(chessBoardRepository, Runnable::run);
    }

    @Test
    void should_not_play_if_not_ai_game() {
        assertFalse(aiGameService.playIfAiTurn(1));
        verifyNoInteractions(chessBoardRepository);
    }

    @Test
    void should_not_play_if_not_ai_turn() {
        aiGameService.registerAiGame(1, Color.BLACK, (gameId, state) -> List.of());

        Player whitePlayer = new Player("Human", Color.WHITE);
        when(chessBoardRepository.getChessBoardService(1)).thenReturn(boardState);
        when(boardState.getCurrentPlayer()).thenReturn(whitePlayer);

        assertFalse(aiGameService.playIfAiTurn(1));
        verify(chessBoardRepository, never()).saveCommand(any());
    }

    @Test
    void should_play_when_ai_turn() {
        List<Command> expectedCommands = List.of(
                PlayMoveCommand.builder().gameId(1).from(Position.e7).to(Position.e5).build(),
                EndTurnCommand.builder().gameId(1).build()
        );
        AiStrategy mockStrategy = (gameId, state) -> expectedCommands;

        aiGameService.registerAiGame(1, Color.BLACK, mockStrategy);

        Player blackPlayer = new Player("AI", Color.BLACK);
        when(chessBoardRepository.getChessBoardService(1)).thenReturn(boardState);
        when(boardState.getCurrentPlayer()).thenReturn(blackPlayer);

        assertTrue(aiGameService.playIfAiTurn(1));
        verify(chessBoardRepository).saveCommand(expectedCommands.get(0));
        verify(chessBoardRepository).saveCommand(expectedCommands.get(1));
    }

    @Test
    void should_discard_move_if_turn_changed_during_computation() {
        // Strategy reads state then returns a move. By the time playIfAiTurn checks
        // post-state, the current player is no longer the AI (user undid end-turn).
        List<Command> aiCommands = List.of(
                PlayMoveCommand.builder().gameId(1).from(Position.e7).to(Position.e5).build(),
                EndTurnCommand.builder().gameId(1).build()
        );
        AiStrategy strategy = (gameId, state) -> aiCommands;
        aiGameService.registerAiGame(1, Color.BLACK, strategy);

        Player blackAi = new Player("AI", Color.BLACK);
        Player whiteHuman = new Player("Human", Color.WHITE);
        // first read: AI turn; second (post-compute) read: back to human (undo happened)
        when(chessBoardRepository.getChessBoardService(1)).thenReturn(boardState);
        when(boardState.getCurrentPlayer()).thenReturn(blackAi, whiteHuman);

        assertFalse(aiGameService.playIfAiTurn(1));
        verify(chessBoardRepository, never()).saveCommand(any());
    }

    @Test
    void schedule_runs_after_commit_callback_when_ai_plays() {
        List<Command> aiCommands = List.of(
                PlayMoveCommand.builder().gameId(1).from(Position.e7).to(Position.e5).build(),
                EndTurnCommand.builder().gameId(1).build()
        );
        AiStrategy strategy = (gameId, state) -> aiCommands;
        aiGameService.registerAiGame(1, Color.BLACK, strategy);

        Player blackAi = new Player("AI", Color.BLACK);
        when(chessBoardRepository.getChessBoardService(1)).thenReturn(boardState);
        when(boardState.getCurrentPlayer()).thenReturn(blackAi);

        Runnable afterCommit = mock(Runnable.class);
        aiGameService.schedulePlayIfAiTurn(1, afterCommit);

        verify(chessBoardRepository).saveCommand(aiCommands.get(0));
        verify(chessBoardRepository).saveCommand(aiCommands.get(1));
        verify(afterCommit).run();
    }

    @Test
    void schedule_does_not_run_after_commit_when_not_ai_game() {
        Runnable afterCommit = mock(Runnable.class);
        aiGameService.schedulePlayIfAiTurn(1, afterCommit);

        verify(afterCommit, never()).run();
        verifyNoInteractions(chessBoardRepository);
    }

    @Test
    void schedule_does_not_run_after_commit_when_not_ai_turn() {
        aiGameService.registerAiGame(1, Color.BLACK, (id, state) -> List.of());
        Player whiteHuman = new Player("Human", Color.WHITE);
        when(chessBoardRepository.getChessBoardService(1)).thenReturn(boardState);
        when(boardState.getCurrentPlayer()).thenReturn(whiteHuman);

        Runnable afterCommit = mock(Runnable.class);
        aiGameService.schedulePlayIfAiTurn(1, afterCommit);

        verify(afterCommit, never()).run();
        verify(chessBoardRepository, never()).saveCommand(any());
    }

    @Test
    void schedule_swallows_strategy_exception() {
        AiStrategy failing = (gameId, state) -> { throw new RuntimeException("boom"); };
        aiGameService.registerAiGame(1, Color.BLACK, failing);
        Player blackAi = new Player("AI", Color.BLACK);
        when(chessBoardRepository.getChessBoardService(1)).thenReturn(boardState);
        when(boardState.getCurrentPlayer()).thenReturn(blackAi);

        Runnable afterCommit = mock(Runnable.class);
        // Should not throw to the caller even if the strategy blows up.
        aiGameService.schedulePlayIfAiTurn(1, afterCommit);

        verify(afterCommit, never()).run();
    }

    @Test
    void should_report_ai_game_correctly() {
        assertFalse(aiGameService.isAiGame(1));
        assertTrue(aiGameService.getAiColor(1).isEmpty());

        aiGameService.registerAiGame(1, Color.BLACK, (id, state) -> List.of());

        assertTrue(aiGameService.isAiGame(1));
        assertEquals(Color.BLACK, aiGameService.getAiColor(1).orElseThrow());
    }
}
