package fr.kubys.command;

import fr.kubys.card.params.TwoPieceCardParam;
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
        Integer gameId = chessBoardRepository.createNewGame();

        // InfiltrationCard is first in CardRegistry, so it's the first card dealt to white
        // It is REPLACE_TURN, so no move is needed first
        String cardName = chessBoardRepository.getChessBoardService(gameId)
                .getCurrentPlayer().getCards().get(0).getName();

        PlayCardWithImmutableParamCommand<TwoPieceCardParam> command = PlayCardWithImmutableParamCommand.<TwoPieceCardParam>builder()
                .gameId(gameId)
                .cardName(cardName)
                .param(Map.of("piece1", "a2", "piece2", "a7"))
                .build();

        assertDoesNotThrow(() -> chessBoardRepository.saveCommand(command));
        assertDoesNotThrow(() -> chessBoardRepository.getChessBoardService(gameId));
    }
}
