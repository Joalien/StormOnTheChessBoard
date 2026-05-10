package fr.kubys.ai;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.command.Command;
import fr.kubys.core.Color;

import java.util.List;

public interface AiStrategy {
    List<Command> decideMove(Integer gameId, ChessBoardReadService boardState);

    /**
     * Decides ENEMY_TURN cards to play during the opponent's turn (in reaction to their
     * move or card play). Default: no reaction. Strategies that support cards can override
     * to scan their hand for {@code ENEMY_TURN_AFTER_MOVE} / {@code ENEMY_TURN_AFTER_CARD}
     * cards and play one if appropriate. Returning an empty list means "do nothing".
     */
    default List<Command> decideEnemyReaction(Integer gameId, ChessBoardReadService boardState, Color aiColor) {
        return List.of();
    }
}
