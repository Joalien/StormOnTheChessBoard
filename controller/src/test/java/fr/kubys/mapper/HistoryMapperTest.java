package fr.kubys.mapper;

import fr.kubys.command.Command;
import fr.kubys.command.EndTurnCommand;
import fr.kubys.command.PlayMoveCommand;
import fr.kubys.command.StartGameCommand;
import fr.kubys.dto.HistoryEntryDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.kubys.core.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class HistoryMapperTest {

    @Test
    void start_game_is_tagged_system() {
        List<HistoryEntryDto> history = HistoryMapper.map(List.of(
                StartGameCommand.builder().gameId(1).build()
        ));

        assertEquals(1, history.size());
        assertEquals("system", history.get(0).getColor());
        assertEquals(0, history.get(0).getIndex());
    }

    @Test
    void white_acts_first_then_swaps_after_end_turn() {
        Command start = StartGameCommand.builder().gameId(1).build();
        Command whiteMove = PlayMoveCommand.builder().gameId(1).from(e2).to(e4).build();
        Command endTurn = EndTurnCommand.builder().gameId(1).build();
        Command blackMove = PlayMoveCommand.builder().gameId(1).from(e7).to(e5).build();

        List<HistoryEntryDto> history = HistoryMapper.map(List.of(start, whiteMove, endTurn, blackMove));

        assertEquals(4, history.size());
        assertEquals("system", history.get(0).getColor());
        assertEquals("white", history.get(1).getColor());
        assertEquals("white", history.get(2).getColor()); // end turn was issued by white
        assertEquals("black", history.get(3).getColor());
    }

    @Test
    void empty_list_returns_empty_history() {
        assertTrue(HistoryMapper.map(List.of()).isEmpty());
    }

    @Test
    void preserves_command_toString_as_action() {
        Command move = PlayMoveCommand.builder().gameId(1).from(e2).to(e4).build();
        List<HistoryEntryDto> history = HistoryMapper.map(List.of(move));

        assertEquals(move.toString(), history.get(0).getAction());
    }

    @Test
    void index_increments_monotonically() {
        Command start = StartGameCommand.builder().gameId(1).build();
        Command move1 = PlayMoveCommand.builder().gameId(1).from(e2).to(e4).build();
        Command endTurn = EndTurnCommand.builder().gameId(1).build();
        Command move2 = PlayMoveCommand.builder().gameId(1).from(e7).to(e5).build();

        List<HistoryEntryDto> history = HistoryMapper.map(List.of(start, move1, endTurn, move2));

        for (int i = 0; i < history.size(); i++) {
            assertEquals(i, history.get(i).getIndex());
        }
    }
}
