package fr.kubys.controller;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.core.Color;
import fr.kubys.dto.PlayerDto;
import fr.kubys.player.Player;
import fr.kubys.repository.ChessBoardRepository;
import fr.kubys.repository.GameNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlayerControllerTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private ChessBoardRepository chessBoardRepository;
    @Mock
    private ChessBoardReadService readService;

    @Test
    void should_return_white_player() {
        final Integer GAME_ID = 1;
        Mockito.when(chessBoardRepository.getChessBoardService(GAME_ID)).thenReturn(readService);
        Mockito.when(readService.getWhite()).thenReturn(new Player("White", Color.WHITE));

        ResponseEntity<PlayerDto> res = this.restTemplate.getForEntity("http://localhost:%s/chessboard/%s/players/white".formatted(port, GAME_ID), PlayerDto.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        Mockito.verify(readService).getWhite();
        Mockito.verify(readService, Mockito.never()).getBlack();
        assertEquals("White", res.getBody().getName());
        assertEquals(Color.WHITE, res.getBody().getColor());
    }

    @Test
    void should_return_black_player() {
        final Integer GAME_ID = 1;
        Mockito.when(chessBoardRepository.getChessBoardService(GAME_ID)).thenReturn(readService);
        Mockito.when(readService.getBlack()).thenReturn(new Player("Black", Color.BLACK));

        ResponseEntity<PlayerDto> res = this.restTemplate.getForEntity("http://localhost:%s/chessboard/%s/players/black".formatted(port, GAME_ID), PlayerDto.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        Mockito.verify(readService, Mockito.never()).getWhite();
        Mockito.verify(readService).getBlack();
        assertEquals("Black", res.getBody().getName());
        assertEquals(Color.BLACK, res.getBody().getColor());
    }

    @Test
    void should_return_bad_request() {
        final Integer GAME_ID = 1;
        Mockito.when(chessBoardRepository.getChessBoardService(GAME_ID)).thenReturn(readService);

        ResponseEntity<PlayerDto> res = this.restTemplate.getForEntity("http://localhost:%s/chessboard/%s/players/toto".formatted(port, GAME_ID), PlayerDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        Mockito.verify(readService, Mockito.never()).getWhite();
        Mockito.verify(readService, Mockito.never()).getBlack();
    }

    @Test
    void should_return_404_game_not_found() {
        final Integer GAME_ID = 1;
        Mockito.when(chessBoardRepository.getChessBoardService(GAME_ID)).thenThrow(new GameNotFoundException(GAME_ID));

        ResponseEntity<Void> res = this.restTemplate.getForEntity("http://localhost:%s/chessboard/%s/players/white".formatted(port, GAME_ID), Void.class);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        Mockito.verifyNoInteractions(readService);
    }
}