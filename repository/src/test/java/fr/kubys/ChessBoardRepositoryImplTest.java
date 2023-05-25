package fr.kubys;

import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.command.StartGameCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.kubys.repository.ChessBoardRepository;
import fr.kubys.repository.ChessBoardRepositoryImpl;

import static org.junit.jupiter.api.Assertions.*;
import static fr.kubys.core.Position.*;

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

        assertTrue(chessBoardRepository.saveCommand(START_GAME_COMMAND));
    }

    @Test
    void should_not_start_a_game_twice() {
        assertTrue(chessBoardRepository.saveCommand(START_GAME_COMMAND));
        assertFalse(chessBoardRepository.saveCommand(START_GAME_COMMAND));
    }

    @Test
    void should_not_be_able_to_move_if_game_not_started() {
        assertFalse(chessBoardRepository.saveCommand(MOVE_GAME_COMMAND));
    }

    @Test
    void should_play_a_game() {
        assertTrue(chessBoardRepository.saveCommand(START_GAME_COMMAND));
        assertTrue(chessBoardRepository.saveCommand(MOVE_GAME_COMMAND));
        assertTrue(chessBoardRepository.saveCommand(END_TURN_COMMAND));
        assertTrue(chessBoardRepository.saveCommand(BLACK_MOVE_COMMAND));

        assertTrue(chessBoardRepository.getChessBoardService(GAME_ID).getPieces().stream().anyMatch(piece -> piece.getPosition().equals(e4)));
    }

    @Test
    void should_not_be_able_to_play_other_color_if_turn_has_not_been_ended() {
        assertTrue(chessBoardRepository.saveCommand(START_GAME_COMMAND));
        assertTrue(chessBoardRepository.saveCommand(MOVE_GAME_COMMAND));
        assertFalse(chessBoardRepository.saveCommand(BLACK_MOVE_COMMAND));
    }

    @Test
    void should_be_empty() {
        assertThrows(IllegalArgumentException.class, () -> chessBoardRepository.getChessBoardService(GAME_ID));
    }

    @Test
    void should_be_different_chessboard_each_time() {
        chessBoardRepository.saveCommand(START_GAME_COMMAND);
        assertNotEquals(chessBoardRepository.getChessBoardService(GAME_ID), chessBoardRepository.getChessBoardService(GAME_ID));
    }
}