package fr.kubys.repository;

import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.command.StartGameCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class ChessBoardRepositoryImplTest {

    public static final int GAME_ID = 1;
    public static final Command START_GAME_COMMAND = StartGameCommand.builder().gameId(GAME_ID).build();
    public static final Command END_TURN_COMMAND = EndTurnCommand.builder().gameId(GAME_ID).build();
    public static final Command MOVE_GAME_COMMAND = PlayMoveCommand.builder().gameId(GAME_ID).from(e2).to(e4).build();
    public static final Command BLACK_MOVE_COMMAND = PlayMoveCommand.builder().gameId(GAME_ID).from(e7).to(e5).build();

    ChessBoardRepository chessBoardRepository;

    @BeforeEach
    void setUp() {
        chessBoardRepository = new ChessBoardRepositoryImpl();
    }

    @Test
    void should_start_a_game() {
        assertDoesNotThrow(() -> chessBoardRepository.createNewGame());
    }

    @Test
    void should_not_start_a_game_twice() {
        assertDoesNotThrow(() -> chessBoardRepository.createNewGame());
        assertThrows(IllegalStateException.class, () -> chessBoardRepository.saveCommand(START_GAME_COMMAND));
    }

    @Test
    void should_play_a_game() {
        assertDoesNotThrow(() -> chessBoardRepository.createNewGame());
        assertDoesNotThrow(() -> chessBoardRepository.saveCommand(MOVE_GAME_COMMAND));
        assertDoesNotThrow(() -> chessBoardRepository.saveCommand(END_TURN_COMMAND));
        assertDoesNotThrow(() -> chessBoardRepository.saveCommand(BLACK_MOVE_COMMAND));

        assertDoesNotThrow(() -> chessBoardRepository.getChessBoardService(GAME_ID).getPieces().stream().anyMatch(piece -> piece.getPosition().equals(e4)));
    }

    @Test
    void should_not_be_able_to_play_other_color_if_turn_has_not_been_ended() {
        assertDoesNotThrow(() -> chessBoardRepository.createNewGame());
        assertDoesNotThrow(() -> chessBoardRepository.saveCommand(MOVE_GAME_COMMAND));
        assertThrows(IllegalStateException.class, () -> chessBoardRepository.saveCommand(BLACK_MOVE_COMMAND));
    }

    @Test
    void game_should_not_be_found() {
        assertThrows(GameNotFoundException.class, () -> chessBoardRepository.getChessBoardService(GAME_ID));
    }

    @Test
    void should_be_different_chessboard_each_time() {
        chessBoardRepository.createNewGame();
        assertNotEquals(chessBoardRepository.getChessBoardService(GAME_ID), chessBoardRepository.getChessBoardService(GAME_ID));
    }

    @Test
    void game_should_not_exist() {
        assertFalse(chessBoardRepository.gameExists(GAME_ID));
    }

    @Test
    void game_should_exist() {
        chessBoardRepository.createNewGame();
        assertTrue(chessBoardRepository.gameExists(GAME_ID));
    }

    @Test
    void should_not_create_game_if_invalid_command() {
        assertThrows(GameNotFoundException.class, () -> chessBoardRepository.saveCommand(MOVE_GAME_COMMAND));
    }
}