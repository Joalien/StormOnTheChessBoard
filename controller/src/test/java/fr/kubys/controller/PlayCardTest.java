package fr.kubys.controller;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.card.BombingCard;
import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;
import fr.kubys.card.params.PositionCardParam;
import fr.kubys.core.Color;
import fr.kubys.player.Player;
import fr.kubys.repository.ChessBoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlayCardTest {
    private static final Integer GAME_ID = 10;
    @Value("${local.server.port}")
    private int port;
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

    private Player getPlayerHaving(Card<? extends CardParam> card) {
        Player player = new Player("White", Color.WHITE);
        player.getCards().add(card);
        return player;
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
}
