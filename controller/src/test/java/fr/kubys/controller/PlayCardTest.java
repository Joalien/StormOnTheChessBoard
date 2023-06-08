package fr.kubys.controller;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.card.*;
import fr.kubys.card.params.*;
import fr.kubys.command.PlayCardCommand;
import fr.kubys.core.Color;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Square;
import fr.kubys.piece.WhitePawn;
import fr.kubys.player.Player;
import fr.kubys.repository.ChessBoardRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

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
    void should_play_bombing_card() {
        Map<String, String> param = Map.of("position", "e4");

        ResponseEntity<Void> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, bombingCard.getId()), param, Void.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        ArgumentCaptor<PlayCardCommand<PositionCardParam>> argumentCaptor = ArgumentCaptor.forClass(PlayCardCommand.class);
        verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
        Assertions.assertEquals(GAME_ID, argumentCaptor.getValue().getGameId());
        assertEquals(new PositionCardParam(e4), argumentCaptor.getValue().getParameters());
        assertEquals(bombingCard, argumentCaptor.getValue().getCard());
    }

    @Test
    void should_play_courtly_love_card() {
        Card<CourtlyLoveCardParam> courtlyLoveCard = new CourtlyLoveCard();
        Mockito.when(readService.getCurrentPlayer()).thenReturn(getPlayerHaving(courtlyLoveCard));
        Knight knight = new Knight(Color.WHITE);
        knight.setSquare(new Square(e4));
        Mockito.when(readService.getPieces()).thenReturn(Set.of(knight));
        Map<String, String> param = Map.of("knight", "e4", "positionToMoveOn", "d6");

        ResponseEntity<String> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, courtlyLoveCard.getId()), param, String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        ArgumentCaptor<PlayCardCommand<CourtlyLoveCardParam>> argumentCaptor = ArgumentCaptor.forClass(PlayCardCommand.class);
        verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
        Assertions.assertEquals(GAME_ID, argumentCaptor.getValue().getGameId());
        assertEquals(new CourtlyLoveCardParam(knight, d6), argumentCaptor.getValue().getParameters());
        assertEquals(new CourtlyLoveCard(), argumentCaptor.getValue().getCard());
    }

    @Test
    void should_play_charge_card() {
        Card<ChargeCardParam> chargeCard = new ChargeCard();
        Mockito.when(readService.getCurrentPlayer()).thenReturn(getPlayerHaving(chargeCard));
        Set<Pawn> collect = Set.of(e2, e3, e4, h7).stream()
                .map(position -> {
                    Pawn p = new WhitePawn();
                    p.setSquare(new Square(position));
                    return p;
                }).collect(Collectors.toSet());
        Mockito.when(readService.getPieces()).thenReturn(new HashSet<>(collect));
        Map<String, Set<String>> param = Map.of("pawns", Set.of("e2", "e3", "e4", "h7"));

        ResponseEntity<String> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, chargeCard.getId()), param, String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        ArgumentCaptor<PlayCardCommand<ChargeCardParam>> argumentCaptor = ArgumentCaptor.forClass(PlayCardCommand.class);
        verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
        Assertions.assertEquals(GAME_ID, argumentCaptor.getValue().getGameId());
        assertEquals(new ChargeCardParam(collect), argumentCaptor.getValue().getParameters());
        assertEquals(new ChargeCard(), argumentCaptor.getValue().getCard());
    }

    @Test
    void should_play_quadrille_card() { // TODO
        Card<QuadrilleCardParam> quadrilleCard = new QuadrilleCard();
        Mockito.when(readService.getCurrentPlayer()).thenReturn(getPlayerHaving(quadrilleCard));
        Map<String, String> param = Map.of("direction", "CLOCKWISE");

        ResponseEntity<String> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, quadrilleCard.getId()), param, String.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        ArgumentCaptor<PlayCardCommand<QuadrilleCardParam>> argumentCaptor = ArgumentCaptor.forClass(PlayCardCommand.class);
        verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
        Assertions.assertEquals(GAME_ID, argumentCaptor.getValue().getGameId());
        assertEquals(new QuadrilleCardParam(QuadrilleCard.Direction.CLOCKWISE), argumentCaptor.getValue().getParameters());
        assertEquals(new QuadrilleCard(), argumentCaptor.getValue().getCard());
    }

    @Test
    void should_return_404_if_card_not_found_in_player_hand() {
        Card<CourtlyLoveCardParam> courtlyLoveCard = new CourtlyLoveCard();

        ResponseEntity<String> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, courtlyLoveCard.getId()), Collections.emptyMap(), String.class);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertTrue(res.getBody().contains(" is not in user hand!"));
    }

    @Test
    void should_return_400_if_no_param() {
        ResponseEntity<Void> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, bombingCard.getId()), null, Void.class);

        assertTrue(res.getStatusCode().is4xxClientError());
    }

    @Test
    void should_return_400_if_no_param_bis() {
        ResponseEntity<Void> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, bombingCard.getId()), "", Void.class);

        assertTrue(res.getStatusCode().is4xxClientError());
    }

    @Test
    void should_return_400_if_param_does_not_include_invalid_field() {
        ResponseEntity<String> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, bombingCard.getId()), new QuadrilleCardParam(QuadrilleCard.Direction.CLOCKWISE), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertTrue(res.getBody().contains(" does not match card parameter "));
    }
}
