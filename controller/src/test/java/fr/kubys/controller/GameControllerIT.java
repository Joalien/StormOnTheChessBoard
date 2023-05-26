package fr.kubys.controller;

import fr.kubys.command.StartGameCommand;
import fr.kubys.dto.ChessBoardDto;
import fr.kubys.repository.ChessBoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan({"fr.kubys"})
class GameControllerIT {

    public static final int EXISTING_GAME_ID = 10;
    public static final int NOT_EXISTING_GAME_ID = 2;
    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ChessBoardRepository chessBoardRepository;

    @Test
    public void should_return_404_if_game_does_not_exist() {
        ResponseEntity<ChessBoardDto> response = this.restTemplate.getForEntity("http://localhost:%s/chessboard/%s".formatted(port, NOT_EXISTING_GAME_ID), ChessBoardDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void should_return_current_game() {
        chessBoardRepository.saveCommand(StartGameCommand.builder().gameId(EXISTING_GAME_ID).build());

        ResponseEntity<ChessBoardDto> response = this.restTemplate.getForEntity("http://localhost:%s/chessboard/%s".formatted(port, EXISTING_GAME_ID), ChessBoardDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ChessBoardDto chessBoardDto = response.getBody();
        assertNotNull(chessBoardDto);
        assertNotNull(chessBoardDto.getBlackPlayer());
        assertNotNull(chessBoardDto.getWhitePlayer());
        assertNotNull(chessBoardDto.getEffects());
        assertTrue(chessBoardDto.getEffects().isEmpty());
        assertFalse(chessBoardDto.getDeck().isEmpty());
        assertEquals(EXISTING_GAME_ID, chessBoardDto.getId());
        assertFalse(chessBoardDto.getPieces().isEmpty());
        assertEquals("wP", chessBoardDto.getPieces().get("e2"));
    }
}