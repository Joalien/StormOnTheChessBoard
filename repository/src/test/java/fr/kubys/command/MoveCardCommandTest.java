package fr.kubys.command;

import fr.kubys.card.params.BarricadeCardParam;
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

        // White moves a pawn first (BarricadeCard is AFTER_TURN, needs a move first)
        chessBoardRepository.saveCommand(PlayMoveCommand.builder().gameId(gameId).from(fr.kubys.core.Position.e2).to(fr.kubys.core.Position.e4).build());

        // BarricadeCard is first in CardRegistry, so it's the first card dealt to white
        String cardName = chessBoardRepository.getChessBoardService(gameId)
                .getCurrentPlayer().getCards().get(0).getName();

        PlayCardWithImmutableParamCommand<BarricadeCardParam> command = PlayCardWithImmutableParamCommand.<BarricadeCardParam>builder()
                .gameId(gameId)
                .cardName(cardName)
                .param(Map.of("from1", "d4", "to1", "e4", "from2", "d5", "to2", "e5"))
                .build();

        assertDoesNotThrow(() -> chessBoardRepository.saveCommand(command));
        assertDoesNotThrow(() -> chessBoardRepository.getChessBoardService(gameId));
    }
}
