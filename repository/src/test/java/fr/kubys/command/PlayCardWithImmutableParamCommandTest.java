package fr.kubys.command;

import fr.kubys.api.ChessBoardService;
import fr.kubys.card.*;
import fr.kubys.card.params.*;
import fr.kubys.core.Color;
import fr.kubys.core.Position;
import fr.kubys.piece.Knight;
import fr.kubys.piece.Pawn;
import fr.kubys.piece.Piece;
import fr.kubys.piece.WhitePawn;
import fr.kubys.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlayCardWithImmutableParamCommandTest {

    private static final Integer GAME_ID = 10;
    private Card<PositionCardParam> bombingCard;
    private final Player currentPlayer = new Player("test", Color.WHITE);
    @Mock
    private ChessBoardService chessBoard;


    @BeforeEach
    void setUp() {
        bombingCard = new BombingCard();
        Mockito.when(chessBoard.getCurrentPlayer()).thenReturn(currentPlayer);
    }

    @Test
    void should_play_bombing_card() {
        currentPlayer.getCards().add(bombingCard);
        PlayCardWithImmutableParamCommand<PositionCardParam> playCardCommand = PlayCardWithImmutableParamCommand.<PositionCardParam>builder()
                .gameId(GAME_ID)
                .param(Map.of("position", "e4"))
                .cardName(bombingCard.getName())
                .build();

        assertDoesNotThrow(() -> playCardCommand.execute(chessBoard));

        ArgumentCaptor<Card<PositionCardParam>> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);
        ArgumentCaptor<PositionCardParam> cardParamArgumentCaptor = ArgumentCaptor.forClass(PositionCardParam.class);
        verify(chessBoard).tryToPlayCard(cardArgumentCaptor.capture(), cardParamArgumentCaptor.capture());
        assertEquals(new PositionCardParam(e4), cardParamArgumentCaptor.getValue());
        assertEquals(bombingCard, cardArgumentCaptor.getValue());
    }

    @Test
    void should_play_courtly_love_card() {
        CourtlyLoveCard courtlyLoveCard = new CourtlyLoveCard();
        currentPlayer.getCards().add(courtlyLoveCard);
        Knight knight = new Knight(Color.WHITE);
        knight.setPosition(e4);
        Mockito.when(chessBoard.getPieces()).thenReturn(Set.of(knight));
        PlayCardWithImmutableParamCommand<CourtlyLoveCardParam> playCardCommand = PlayCardWithImmutableParamCommand.<CourtlyLoveCardParam>builder()
                .gameId(GAME_ID)
                .param(Map.of("knight", "e4", "positionToMoveOn", "d6"))
                .cardName(courtlyLoveCard.getName())
                .build();

        assertDoesNotThrow(() -> playCardCommand.execute(chessBoard));

        ArgumentCaptor<Card<CourtlyLoveCardParam>> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);
        ArgumentCaptor<CourtlyLoveCardParam> cardParamArgumentCaptor = ArgumentCaptor.forClass(CourtlyLoveCardParam.class);
        verify(chessBoard).tryToPlayCard(cardArgumentCaptor.capture(), cardParamArgumentCaptor.capture());
        assertEquals(new CourtlyLoveCardParam(getPieceOnSquare(e4), d6), cardParamArgumentCaptor.getValue());
        assertEquals(new CourtlyLoveCard(), cardArgumentCaptor.getValue());
    }

    private <T extends Piece> T getPieceOnSquare(Position position) {
        return (T) chessBoard.getPieces().stream()
                .filter(piece -> piece.getPosition() == position)
                .findFirst().get();
    }

    @Test
    void should_play_charge_card() {
        Card<ChargeCardParam> chargeCard = new ChargeCard();
        currentPlayer.getCards().add(chargeCard);
        Mockito.when(chessBoard.getPieces()).thenReturn(Set.of("e2", "e3", "e4", "h7").stream()
                .map(position -> {
                    Pawn p = new WhitePawn();
                    p.setPosition(Position.valueOf(position));
                    return p;
                }).collect(Collectors.toSet()));
        PlayCardWithImmutableParamCommand<ChargeCardParam> playCardCommand = PlayCardWithImmutableParamCommand.<ChargeCardParam>builder()
                .gameId(GAME_ID)
                .param(Map.of("pawns", Set.of("e2", "e3", "e4", "h7")))
                .cardName(chargeCard.getName())
                .build();

        assertDoesNotThrow(() -> playCardCommand.execute(chessBoard));

        ArgumentCaptor<Card<ChargeCardParam>> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);
        ArgumentCaptor<ChargeCardParam> cardParamArgumentCaptor = ArgumentCaptor.forClass(ChargeCardParam.class);
        verify(chessBoard).tryToPlayCard(cardArgumentCaptor.capture(), cardParamArgumentCaptor.capture());
        assertEquals(new ChargeCardParam(Set.of(getPieceOnSquare(e2),getPieceOnSquare(e3),getPieceOnSquare(e4),getPieceOnSquare(h7))), cardParamArgumentCaptor.getValue());
        assertEquals(new ChargeCard(), cardArgumentCaptor.getValue());
    }

    @Test
    void should_play_quadrille_card() {
        Card<QuadrilleCardParam> quadrilleCard = new QuadrilleCard();
        currentPlayer.getCards().add(quadrilleCard);
        PlayCardWithImmutableParamCommand<QuadrilleCardParam> playCardCommand = PlayCardWithImmutableParamCommand.<QuadrilleCardParam>builder()
                .gameId(GAME_ID)
                .param(Map.of("direction", "CLOCKWISE"))
                .cardName(quadrilleCard.getName())
                .build();

        assertDoesNotThrow(() -> playCardCommand.execute(chessBoard));

        ArgumentCaptor<Card<QuadrilleCardParam>> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);
        ArgumentCaptor<QuadrilleCardParam> cardParamArgumentCaptor = ArgumentCaptor.forClass(QuadrilleCardParam.class);
        verify(chessBoard).tryToPlayCard(cardArgumentCaptor.capture(), cardParamArgumentCaptor.capture());
        assertEquals(new QuadrilleCardParam(QuadrilleCard.Direction.CLOCKWISE), cardParamArgumentCaptor.getValue());
        assertEquals(new QuadrilleCard(), cardArgumentCaptor.getValue());
    }

    @Test
    void should_return_404_if_card_not_found_in_player_hand() {
        Card<QuadrilleCardParam> quadrilleCard = new QuadrilleCard();
        PlayCardWithImmutableParamCommand<QuadrilleCardParam> playCardCommand = PlayCardWithImmutableParamCommand.<QuadrilleCardParam>builder()
                .gameId(GAME_ID)
                .param(Map.of("direction", "CLOCKWISE"))
                .cardName(quadrilleCard.getName())
                .build();

        assertThrows(CardNotFoundException.class, () -> playCardCommand.execute(chessBoard));
    }

    @Test
    void should_return_400_if_param_does_not_include_invalid_field() {
        Card<QuadrilleCardParam> quadrilleCard = new QuadrilleCard();
        currentPlayer.getCards().add(quadrilleCard);
        PlayCardWithImmutableParamCommand<QuadrilleCardParam> playCardCommand = PlayCardWithImmutableParamCommand.<QuadrilleCardParam>builder()
                .gameId(GAME_ID)
                .param(Map.of("position", "e4"))
                .cardName(quadrilleCard.getName())
                .build();

        assertThrows(CardParamException.class, () -> playCardCommand.execute(chessBoard));
    }


}