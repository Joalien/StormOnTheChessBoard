package fr.kubys.mapper;

import fr.kubys.card.Card;
import fr.kubys.card.CardRegistry;
import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayCardWithImmutableParamCommand;
import fr.kubys.command.StartGameCommand;
import fr.kubys.core.Color;
import fr.kubys.dto.HistoryEntryDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class HistoryMapper {

    private static final Map<String, Card<?>> CARDS_BY_NAME = CardRegistry.createAllCards().stream()
            .collect(Collectors.toMap(Card::getName, c -> c, (a, b) -> a));

    private HistoryMapper() {}

    /**
     * Walks the committed commands in order and tags each with the player whose turn it
     * was when the action was issued. {@link StartGameCommand} is tagged "system"; turn
     * ownership swaps after each {@link EndTurnCommand}. Card-play entries also carry the
     * card's English name, display name, description, type and parameters so the frontend
     * can render the card in its side panel for inspection. Best-effort: ENEMY_TURN cards
     * and other off-turn actions are still tagged with the side currently to move.
     */
    public static List<HistoryEntryDto> map(List<Command> commands) {
        List<HistoryEntryDto> history = new ArrayList<>();
        Color current = Color.WHITE;
        int index = 0;
        for (Command command : commands) {
            String color = command instanceof StartGameCommand ? "system" : current.name().toLowerCase();
            HistoryEntryDto.HistoryEntryDtoBuilder builder = HistoryEntryDto.builder()
                    .index(index++)
                    .color(color)
                    .action(command.toString())
                    .instant(command.getInstant().toString());
            if (command instanceof PlayCardWithImmutableParamCommand<?> cardCommand) {
                Card<?> card = CARDS_BY_NAME.get(cardCommand.getCardName());
                if (card != null) {
                    builder.cardEnglishName(card.getClass().getSimpleName())
                            .cardName(card.getName())
                            .cardDescription(card.getDescription())
                            .cardType(card.getType().name())
                            .cardParams(cardCommand.getParam());
                }
            }
            history.add(builder.build());
            if (command instanceof EndTurnCommand) {
                current = current.opposite();
            }
        }
        return history;
    }
}
