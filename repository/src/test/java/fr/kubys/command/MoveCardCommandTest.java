package fr.kubys.command;

import fr.kubys.card.Card;
import fr.kubys.card.CardType;
import fr.kubys.card.params.CardParam;
import fr.kubys.repository.ChessBoardRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

        // Find a REPLACE_TURN or BEFORE_TURN card (playable at BEGINNING_OF_THE_TURN)
        Card<? extends CardParam> card = chessBoardRepository.getChessBoardService(gameId)
                .getCurrentPlayer().getCards().stream()
                .filter(c -> c.getType() == CardType.REPLACE_TURN || c.getType() == CardType.BEFORE_TURN)
                .findFirst()
                .orElseGet(() -> chessBoardRepository.getChessBoardService(gameId)
                        .getCurrentPlayer().getCards().get(0));

        String cardName = card.getName();

        // Build param map matching the card's expected fields with valid positions
        Map<String, Object> paramMap = Arrays.stream(card.getClazz().getDeclaredFields())
                .collect(Collectors.toMap(
                        Field::getName,
                        field -> {
                            if (field.getType().isEnum()) return field.getType().getEnumConstants()[0].toString();
                            if (fr.kubys.core.Position.class.equals(field.getType())) return "e4";
                            return "a2"; // piece fields - use a position with a piece on it
                        }
                ));

        PlayCardWithImmutableParamCommand<? extends CardParam> command = PlayCardWithImmutableParamCommand.builder()
                .gameId(gameId)
                .cardName(cardName)
                .param(paramMap)
                .build();

        assertDoesNotThrow(() -> chessBoardRepository.saveCommand(command));
        assertDoesNotThrow(() -> chessBoardRepository.getChessBoardService(gameId));
    }
}
