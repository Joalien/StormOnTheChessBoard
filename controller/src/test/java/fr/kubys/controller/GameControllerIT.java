package fr.kubys.controller;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.dto.ChessBoardDto;
import fr.kubys.repository.ChessBoardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan({"fr.kubys"})
class GameControllerIT {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ChessBoardRepository chessBoardRepository;

    @Test
    void should_create_new_game() {
        ResponseEntity<Integer> response = this.restTemplate.postForEntity("http://localhost:%s/chessboard".formatted(port), null, Integer.class);
        Integer gameId = response.getBody();
        ChessBoardReadService chessBoardService = chessBoardRepository.getChessBoardService(gameId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(gameId);
        assertNotNull(chessBoardService);
        assertNotNull(chessBoardService.getBlack());
        assertNotNull(chessBoardService.getWhite());
        assertNotNull(chessBoardService.getEffects());
        assertTrue(chessBoardService.getEffects().isEmpty());
        assertFalse(chessBoardService.getCards().isEmpty());
        assertEquals(32, chessBoardService.getPieces().size());
    }

    @Test
    public void should_return_404_if_game_does_not_exist() {
        Integer notExistingGameId = chessBoardRepository.createNewGame() + 1;
        ResponseEntity<ChessBoardDto> response = this.restTemplate.getForEntity("http://localhost:%s/chessboard/%s".formatted(port, notExistingGameId), ChessBoardDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void should_return_404_if_play_a_move_on_a_non_existing_game() {
        Integer notExistingGameId = chessBoardRepository.createNewGame() + 1;
        ResponseEntity<ChessBoardDto> response = this.restTemplate.postForEntity("http://localhost:%s/chessboard/%s/move/%s/to/%s".formatted(port, notExistingGameId, "e2", "e4"), null, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void should_return_current_game() {
        Integer gameId = chessBoardRepository.createNewGame();

        ResponseEntity<ChessBoardDto> response = this.restTemplate.getForEntity("http://localhost:%s/chessboard/%s".formatted(port, gameId), ChessBoardDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ChessBoardDto chessBoardDto = response.getBody();
        assertNotNull(chessBoardDto);
        assertNotNull(chessBoardDto.getBlackPlayer());
        assertNotNull(chessBoardDto.getWhitePlayer());
        assertNotNull(chessBoardDto.getEffects());
        assertTrue(chessBoardDto.getEffects().isEmpty());
        assertFalse(chessBoardDto.getDeck().isEmpty());
        assertEquals(gameId, chessBoardDto.getId());
        assertFalse(chessBoardDto.getPieces().isEmpty());
        assertEquals("wP", chessBoardDto.getPieces().get("e2"));
    }
}