package fr.kubys.controller;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.card.BombingCard;
import fr.kubys.card.Card;
import fr.kubys.card.CourtlyLoveCard;
import fr.kubys.card.QuadrilleCard;
import fr.kubys.card.params.CardParam;
import fr.kubys.card.params.CourtlyLoveCardParam;
import fr.kubys.card.params.PositionCardParam;
import fr.kubys.card.params.QuadrilleCardParam;
import fr.kubys.command.PlayCardCommand;
import fr.kubys.core.Color;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Square;
import fr.kubys.player.Player;
import fr.kubys.repository.ChessBoardRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static fr.kubys.core.Position.d6;
import static fr.kubys.core.Position.e4;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlayCardTest {
    @Value("${local.server.port}")
    private int port;

    private static final Integer GAME_ID = 10;

    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private ChessBoardRepository chessBoardRepository;
    @Mock
    private ChessBoardReadService readService;
    private Card<PositionCardParam> bombingCard;

    @BeforeEach
    void setUp() {
        bombingCard = new BombingCard();
        Mockito.when(chessBoardRepository.getChessBoardService(GAME_ID)).thenReturn(readService);
        Mockito.when(readService.getCurrentPlayer()).thenReturn(getPlayerHaving(bombingCard));
    }

    @Test
    void should_play_courtly_love_card() {
        Card<CourtlyLoveCardParam> courtlyLoveCard = new CourtlyLoveCard();
        Mockito.when(readService.getCurrentPlayer()).thenReturn(getPlayerHaving(courtlyLoveCard));
        Knight knight = new Knight(Color.WHITE);
        knight.setSquare(new Square(e4));
        Mockito.when(readService.getPieces()).thenReturn(Set.of(knight));
        Map<String, String> param = Map.of("knight", "e4", "positionToMoveOn", "d6");

        ResponseEntity<String> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, courtlyLoveCard.getName()), param, String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        ArgumentCaptor<PlayCardCommand<CourtlyLoveCardParam>> argumentCaptor = ArgumentCaptor.forClass(PlayCardCommand.class);
        verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
        Assertions.assertEquals(GAME_ID, argumentCaptor.getValue().getGameId());
        assertEquals(new CourtlyLoveCardParam(knight, d6), argumentCaptor.getValue().getParameters());
        assertEquals(new CourtlyLoveCard(), argumentCaptor.getValue().getCard());
    }

    @Test
    void should_play_bombing_card() {
        Map<String, String> param = Map.of("position", "e4");

        ResponseEntity<Void> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, bombingCard.getName()), param, Void.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        ArgumentCaptor<PlayCardCommand<PositionCardParam>> argumentCaptor = ArgumentCaptor.forClass(PlayCardCommand.class);
        verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
        Assertions.assertEquals(GAME_ID, argumentCaptor.getValue().getGameId());
        assertEquals(new PositionCardParam(e4), argumentCaptor.getValue().getParameters());
        assertEquals(bombingCard, argumentCaptor.getValue().getCard());
    }

    @Test
    void should_return_404_if_card_not_found_in_player_hand() {
        Card<CourtlyLoveCardParam> courtlyLoveCard = new CourtlyLoveCard();

        ResponseEntity<String> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, courtlyLoveCard.getName()), Collections.emptyMap(), String.class);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertTrue(res.getBody().contains("Amour courtois not in user hand!"));
    }

    @Test
    void should_return_400_if_no_param() {
        ResponseEntity<Void> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, bombingCard.getName()), null, Void.class);

        assertTrue(res.getStatusCode().is4xxClientError());
    }

    @Test
    void should_return_400_if_no_param_bis() {
        ResponseEntity<Void> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, bombingCard.getName()), "", Void.class);

        assertTrue(res.getStatusCode().is4xxClientError());
    }

    @Test
    void should_return_400_if_param_does_not_include_invalid_field() {
        ResponseEntity<String> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, bombingCard.getName()), new QuadrilleCardParam(QuadrilleCard.Direction.CLOCKWISE), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertTrue(res.getBody().contains(" does not match card parameter "));
    }

    private Player getPlayerHaving(Card<? extends CardParam> card) {
        Player player = new Player("White", Color.WHITE);
        player.getCards().add(card);
        return player;
    }
}
