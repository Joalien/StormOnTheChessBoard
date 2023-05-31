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
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayCardCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.core.Color;
import fr.kubys.dto.card.PositionCardParamDto;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Square;
import fr.kubys.player.Player;
import fr.kubys.repository.ChessBoardRepository;
import fr.kubys.repository.GameNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerTest {

    @Value("${local.server.port}")
    private int port;

    private static final Integer GAME_ID = 10;

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

    @Nested
    class play_a_card {

        @Mock
        private ChessBoardReadService readService;
        private Card<PositionCardParam> bombingCard;

        @BeforeEach
        void setUp() {
            Mockito.when(chessBoardRepository.getChessBoardService(GAME_ID)).thenReturn(readService);
            Mockito.when(readService.getCurrentPlayer()).thenReturn(getPlayerHaving(new BombingCard()));
            bombingCard = new BombingCard();
        }

        @Test
        void should_return_404_if_card_not_found_in_player_hand() {
            Card<CourtlyLoveCardParam> courtlyLoveCard = new CourtlyLoveCard();

            ResponseEntity<String> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, courtlyLoveCard.getName()), Collections.emptyMap(), String.class);

            assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
            assertTrue(res.getBody().contains("not in user hand!"));
        }

        @Test
        void should_play_courtly_love_card() {
            Card<CourtlyLoveCardParam> courtlyLoveCard = new CourtlyLoveCard();
            Mockito.when(readService.getCurrentPlayer()).thenReturn(getPlayerHaving(courtlyLoveCard));
            Knight knight = new Knight(Color.WHITE);
            knight.setSquare(new Square(e4));
            Mockito.when(readService.getPieces()).thenReturn(Set.of(knight));
            Map<String, String> param = Map.of("knight", "e4", "positionToMoveOn", "d6");

            ResponseEntity<Void> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, courtlyLoveCard.getName()), param, Void.class);

            assertEquals(HttpStatus.OK, res.getStatusCode());
            ArgumentCaptor<PlayCardCommand<CourtlyLoveCardParam>> argumentCaptor = ArgumentCaptor.forClass(PlayCardCommand.class);
            verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
            assertEquals(GAME_ID, argumentCaptor.getValue().getGameId());
            assertEquals(new CourtlyLoveCardParam(knight, d6), argumentCaptor.getValue().getParameters());
            assertEquals(new CourtlyLoveCard(), argumentCaptor.getValue().getCard());
        }

        @Test
        void should_play_bombing_card() {
            CardParam param = new PositionCardParam(e4);

            ResponseEntity<Void> res = restTemplate.postForEntity("http://localhost:%s/chessboard/%s/card/%s".formatted(port, GAME_ID, bombingCard.getName()), param, Void.class);

            assertEquals(HttpStatus.OK, res.getStatusCode());
            ArgumentCaptor<PlayCardCommand<PositionCardParam>> argumentCaptor = ArgumentCaptor.forClass(PlayCardCommand.class);
            verify(chessBoardRepository).saveCommand(argumentCaptor.capture());
            assertEquals(GAME_ID, argumentCaptor.getValue().getGameId());
            assertEquals(param, argumentCaptor.getValue().getParameters());
            assertEquals(bombingCard, argumentCaptor.getValue().getCard());
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
        }

        private Player getPlayerHaving(Card<? extends CardParam> card) {
            Player player = new Player("White", Color.WHITE);
            player.getCards().add(card);
            return player;
        }
    }
}