package fr.kubys.controller;

import fr.kubys.ai.AiGameService;
import fr.kubys.ai.AiStrategy;
import fr.kubys.ai.MaterialStrategy;
import fr.kubys.ai.RandomMoveStrategy;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Color;
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

import java.util.Map;

import static fr.kubys.core.Position.e2;
import static fr.kubys.core.Position.e4;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @MockBean
    private AiGameService aiGameService;

    @BeforeEach
    void setUp() {
        Mockito.clearInvocations(chessBoardRepository, aiGameService);
    }

    @Test
    void should_create_a_move_command() {
        ResponseEntity<Object> objectResponseEntity = this.restTemplate.postForEntity("http://localhost:%s/api/chessboard/%s/move/%s/to/%s".formatted(port, GAME_ID, "e2", "e4"), null, null);

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
        ResponseEntity<Object> objectResponseEntity = this.restTemplate.postForEntity("http://localhost:%s/api/chessboard/%s/endTurn".formatted(port, GAME_ID), null, null);

        assertEquals(HttpStatus.OK, objectResponseEntity.getStatusCode());
        ArgumentCaptor<EndTurnCommand> argumentCaptor = ArgumentCaptor.forClass(EndTurnCommand.class);
        verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
        assertEquals(GAME_ID, argumentCaptor.getValue().getGameId());
    }

    @Test
    void should_register_random_strategy_when_requested() {
        Mockito.when(chessBoardRepository.createNewGame()).thenReturn(GAME_ID);

        ResponseEntity<Map> response = this.restTemplate.postForEntity(
                "http://localhost:%s/api/chessboard/ai?strategy=random".formatted(port), null, Map.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ArgumentCaptor<AiStrategy> captor = ArgumentCaptor.forClass(AiStrategy.class);
        verify(aiGameService).registerAiGame(eq(GAME_ID), eq(Color.BLACK), captor.capture());
        assertInstanceOf(RandomMoveStrategy.class, captor.getValue());
    }

    @Test
    void should_register_material_strategy_when_requested() {
        Mockito.when(chessBoardRepository.createNewGame()).thenReturn(GAME_ID);

        ResponseEntity<Map> response = this.restTemplate.postForEntity(
                "http://localhost:%s/api/chessboard/ai?strategy=material".formatted(port), null, Map.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ArgumentCaptor<AiStrategy> captor = ArgumentCaptor.forClass(AiStrategy.class);
        verify(aiGameService).registerAiGame(eq(GAME_ID), eq(Color.BLACK), captor.capture());
        assertInstanceOf(MaterialStrategy.class, captor.getValue());
    }

    @Test
    void should_register_default_strategy_when_no_param() {
        Mockito.when(chessBoardRepository.createNewGame()).thenReturn(GAME_ID);

        ResponseEntity<Map> response = this.restTemplate.postForEntity(
                "http://localhost:%s/api/chessboard/ai".formatted(port), null, Map.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(aiGameService).registerAiGame(eq(GAME_ID), eq(Color.BLACK), any(AiStrategy.class));
    }

    @Test
    void should_register_default_strategy_when_unknown_param() {
        Mockito.when(chessBoardRepository.createNewGame()).thenReturn(GAME_ID);

        ResponseEntity<Map> response = this.restTemplate.postForEntity(
                "http://localhost:%s/api/chessboard/ai?strategy=bogus".formatted(port), null, Map.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ArgumentCaptor<AiStrategy> captor = ArgumentCaptor.forClass(AiStrategy.class);
        verify(aiGameService).registerAiGame(eq(GAME_ID), eq(Color.BLACK), captor.capture());
        // default is stockfish-or-material depending on environment; just make sure it's not the random one
        assertEquals(false, captor.getValue() instanceof RandomMoveStrategy);
    }

    @Test
    void fail_to_end_turn_of_not_existing_game() {
        Mockito.doThrow(new GameNotFoundException(GAME_ID)).when(chessBoardRepository).saveCommand(any());

        ResponseEntity<Object> objectResponseEntity = this.restTemplate.postForEntity("http://localhost:%s/api/chessboard/%s/endTurn".formatted(port, GAME_ID), null, null);

        assertEquals(HttpStatus.NOT_FOUND, objectResponseEntity.getStatusCode());
    }

}