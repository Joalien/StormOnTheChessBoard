package fr.kubys.command;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.card.Card;
import fr.kubys.card.LightweightSquadCard;
import fr.kubys.card.params.LightweightSquadCardParam;
import fr.kubys.core.Position;
import fr.kubys.piece.Pawn;
import fr.kubys.repository.ChessBoardRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MoveCardCommandTest {

    ChessBoardRepositoryImpl chessBoardRepository;

    @BeforeEach
    void setUp() {
        chessBoardRepository = new ChessBoardRepositoryImpl();
    }

    @Test
    void card_parameter_should_act_as_immutable() {
        Integer gameId = getGameWithLightweightSquadCardInTheHandOfCurrentPlayer();
        String cardName = chessBoardRepository.getChessBoardService(gameId).getCurrentPlayer().getCards().stream()
                .filter(card -> card.getClass() == LightweightSquadCard.class)
                .findAny().orElseThrow()
                .getName();
        PlayCardWithImmutableParamCommand<LightweightSquadCardParam> command = PlayCardWithImmutableParamCommand.<LightweightSquadCardParam>builder()
                .gameId(gameId)
                .cardName(cardName)
                .param(Map.of("pawn1", "e2", "pawn2", "d2"))
                .build();

        assertDoesNotThrow(() -> chessBoardRepository.saveCommand(command));
        assertDoesNotThrow(() -> chessBoardRepository.getChessBoardService(gameId));
    }

    private Integer getGameWithLightweightSquadCardInTheHandOfCurrentPlayer() { // FIXME
        Integer newGame;
        do {
            newGame = chessBoardRepository.createNewGame();
        } while (chessBoardRepository.getChessBoardService(newGame).getCurrentPlayer().getCards().stream()
                .map(Card::getClass)
                .noneMatch(aClass -> aClass == LightweightSquadCard.class));
            return newGame;
    }

    private static Pawn getPawnOn(ChessBoardReadService chessBoardService, Position position) {
        return chessBoardService.getPieces().stream()
                .filter(Pawn.class::isInstance)
                .map(Pawn.class::cast)
                .filter(piece -> piece.getPosition()
                .equals(position)).findAny().orElseThrow();
    }
}