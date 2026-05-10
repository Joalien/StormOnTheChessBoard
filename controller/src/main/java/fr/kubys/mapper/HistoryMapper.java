package fr.kubys.mapper;

import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.StartGameCommand;
import fr.kubys.core.Color;
import fr.kubys.dto.HistoryEntryDto;

import java.util.ArrayList;
import java.util.List;

public final class HistoryMapper {

    private HistoryMapper() {}

    /**
     * Walks the committed commands in order and tags each with the player whose turn it
     * was when the action was issued. {@link StartGameCommand} is tagged "system"; turn
     * ownership swaps after each {@link EndTurnCommand}. Best-effort: ENEMY_TURN cards
     * and other off-turn actions are still tagged with the side currently to move, which
     * is sufficient for the debug view's purpose.
     */
    public static List<HistoryEntryDto> map(List<Command> commands) {
        List<HistoryEntryDto> history = new ArrayList<>();
        Color current = Color.WHITE;
        int index = 0;
        for (Command command : commands) {
            String color = command instanceof StartGameCommand ? "system" : current.name().toLowerCase();
            history.add(HistoryEntryDto.builder()
                    .index(index++)
                    .color(color)
                    .action(command.toString())
                    .instant(command.getInstant().toString())
                    .build());
            if (command instanceof EndTurnCommand) {
                current = current.opposite();
            }
        }
        return history;
    }
}
