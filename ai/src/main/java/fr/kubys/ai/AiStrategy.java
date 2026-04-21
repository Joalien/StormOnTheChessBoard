package fr.kubys.ai;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.command.Command;

import java.util.List;

public interface AiStrategy {
    List<Command> decideMove(Integer gameId, ChessBoardReadService boardState);
}
