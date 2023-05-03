import command.Command;
import command.PlayMoveCommand;
import command.StartGameCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessBoardRepositoryImplTest {

    public static final Command START_GAME_COMMAND = StartGameCommand.builder().gameId(1).build();
    public static final Command MOVE_GAME_COMMAND = PlayMoveCommand.builder().gameId(1).from("e2").to("e4").build();

    ChessBoardRepository chessBoardRepository;

    @BeforeEach
    void setUp() {
        chessBoardRepository = new ChessBoardRepositoryImpl();
    }

    @Test
    void should_start_a_game() {

        assertTrue(chessBoardRepository.saveCommand(1, START_GAME_COMMAND));
    }
    
    @Test
    void should_not_start_a_game_twice() {
        assertTrue(chessBoardRepository.saveCommand(1, START_GAME_COMMAND));
        assertFalse(chessBoardRepository.saveCommand(1, START_GAME_COMMAND));
    }

    @Test
    void should_not_be_able_to_move_if_game_not_started() {
        assertFalse(chessBoardRepository.saveCommand(1, MOVE_GAME_COMMAND));
    }

    @Test
    void should_be_empty() {
        assertThrows(IllegalArgumentException.class, () -> chessBoardRepository.getChessBoardService(1));
    }

    @Test
    void should_be_different_chessboard_each_time() {
        chessBoardRepository.saveCommand(1, START_GAME_COMMAND);
        assertNotEquals(chessBoardRepository.getChessBoardService(1), chessBoardRepository.getChessBoardService(1));
    }
}