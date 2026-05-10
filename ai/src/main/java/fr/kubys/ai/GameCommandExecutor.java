package fr.kubys.ai;

import fr.kubys.command.Command;

/**
 * Hides the persistence layer from the AI. The AI submits the commands it has decided to
 * play; the implementation chooses how to persist them (typically by saving to the
 * repository). Decoupling this from {@code ChessBoardRepository} keeps the AI module
 * focused on decision-making instead of persistence concerns.
 */
@FunctionalInterface
public interface GameCommandExecutor {
    void execute(Command command);
}
