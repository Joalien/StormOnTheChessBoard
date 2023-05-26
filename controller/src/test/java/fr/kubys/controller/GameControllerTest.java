package fr.kubys.controller;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.dto.ChessBoardDto;
import fr.kubys.repository.ChessBoardRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static fr.kubys.core.Position.e2;
import static fr.kubys.core.Position.e4;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private ChessBoardRepository chessBoardRepository;

    @Test
    void should_create_a_move_command() {
        Integer gameId = 10;
        Mockito.clearInvocations(chessBoardRepository);

        ResponseEntity<Object> objectResponseEntity = this.restTemplate.postForEntity("http://localhost:%s/chessboard/%s/move/%s/to/%s".formatted(port, gameId, "e2", "e4"), null, null);

        assertEquals(HttpStatus.OK, objectResponseEntity.getStatusCode());
        verify(chessBoardRepository).getChessBoardService(gameId);
        ArgumentCaptor<PlayMoveCommand> argumentCaptor = ArgumentCaptor.forClass(PlayMoveCommand.class);
        verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
        PlayMoveCommand capturedArgument = argumentCaptor.getValue();
        assertEquals(e2, capturedArgument.getFrom());
        assertEquals(e4, capturedArgument.getTo());
        assertEquals(gameId, capturedArgument.getGameId());
    }
    
    @Test
    void should_end_turn() {
        Integer gameId = 10;
        Mockito.clearInvocations(chessBoardRepository);

        ResponseEntity<Object> objectResponseEntity = this.restTemplate.postForEntity("http://localhost:%s/chessboard/%s/endTurn".formatted(port, gameId), null, null);

        assertEquals(HttpStatus.OK, objectResponseEntity.getStatusCode());
        verify(chessBoardRepository).getChessBoardService(gameId);
        ArgumentCaptor<EndTurnCommand> argumentCaptor = ArgumentCaptor.forClass(EndTurnCommand.class);
        verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
        assertEquals(gameId, argumentCaptor.getValue().getGameId());
    }
}