import command.Command;
import command.PlayMoveCommand;
import command.StartGameCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessBoardRepositoryImplTest {

    public static final Command START_GAME_COMMAND = StartGameCommand.builder().gameId(1).build();
    public static final Command MOVE_GAME_COMMAND = PlayMoveCommand.builder().gameId(1).from("e2").to("e4").build();

    @Test
    void should_start_a_game() {
        ChessBoardRepository chessBoardRepository = new ChessBoardRepositoryImpl();
        assertTrue(chessBoardRepository.saveCommand(1, START_GAME_COMMAND));
    }
    
    @Test
    void should_not_start_a_game_twice() {
        ChessBoardRepository chessBoardRepository = new ChessBoardRepositoryImpl();
        assertTrue(chessBoardRepository.saveCommand(1, START_GAME_COMMAND));
        assertFalse(chessBoardRepository.saveCommand(1, START_GAME_COMMAND));
    }

    @Test
    void should_not_be_able_to_move_if_game_not_started() {
        ChessBoardRepository chessBoardRepository = new ChessBoardRepositoryImpl();
        assertFalse(chessBoardRepository.saveCommand(1, MOVE_GAME_COMMAND));
    }
}