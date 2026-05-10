package fr.kubys.repository;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.ChessBoard;
import fr.kubys.command.Command;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public interface ChessBoardRepository {

    Integer createNewGame();

    Integer createCustomGame(Supplier<ChessBoard> boardFactory);

    void saveCommand(Command cbs);

    ChessBoardReadService getChessBoardService(Integer gameId);

    void undoLastCommand(Integer gameId);

    /**
     * Replays all committed commands plus the supplied hypothetical commands on a fresh
     * service, without mutating the persisted store. Used by the AI to explore the value
     * of candidate moves and card plays.
     */
    ChessBoardReadService simulate(Integer gameId, List<Command> hypothetical);

    /**
     * Returns the immutable command history for the supplied game in chronological order.
     * Used by the API to surface a play-by-play history to the frontend for debugging and
     * visualization.
     */
    List<Command> getCommands(Integer gameId);

    boolean gameExists(Integer gameId);

    Set<Integer> getGameIds();
}
