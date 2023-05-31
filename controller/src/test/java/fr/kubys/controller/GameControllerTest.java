package fr.kubys.controller;

import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.repository.ChessBoardRepository;
import fr.kubys.repository.GameNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static fr.kubys.core.Position.e2;
import static fr.kubys.core.Position.e4;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerTest {

    private static final Integer GAME_ID = 10;
    @Value("${local.server.port}")
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private ChessBoardRepository chessBoardRepository;

    @BeforeEach
    void setUp() {
        Mockito.clearInvocations(chessBoardRepository);
    }

    @Test
    void should_create_a_move_command() {
        ResponseEntity<Object> objectResponseEntity = this.restTemplate.postForEntity("http://localhost:%s/chessboard/%s/move/%s/to/%s".formatted(port, GAME_ID, "e2", "e4"), null, null);

        assertEquals(HttpStatus.OK, objectResponseEntity.getStatusCode());
        ArgumentCaptor<PlayMoveCommand> argumentCaptor = ArgumentCaptor.forClass(PlayMoveCommand.class);
        verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
        PlayMoveCommand capturedArgument = argumentCaptor.getValue();
        assertEquals(e2, capturedArgument.getFrom());
        assertEquals(e4, capturedArgument.getTo());
        assertEquals(GAME_ID, capturedArgument.getGameId());
    }

    @Test
    void should_end_turn() {
        ResponseEntity<Object> objectResponseEntity = this.restTemplate.postForEntity("http://localhost:%s/chessboard/%s/endTurn".formatted(port, GAME_ID), null, null);

        assertEquals(HttpStatus.OK, objectResponseEntity.getStatusCode());
        ArgumentCaptor<EndTurnCommand> argumentCaptor = ArgumentCaptor.forClass(EndTurnCommand.class);
        verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
        assertEquals(GAME_ID, argumentCaptor.getValue().getGameId());
    }

    @Test
    void fail_to_end_turn_of_not_existing_game() {
        Mockito.doThrow(new GameNotFoundException(GAME_ID)).when(chessBoardRepository).saveCommand(any());

        ResponseEntity<Object> objectResponseEntity = this.restTemplate.postForEntity("http://localhost:%s/chessboard/%s/endTurn".formatted(port, GAME_ID), null, null);

        assertEquals(HttpStatus.NOT_FOUND, objectResponseEntity.getStatusCode());
    }

}