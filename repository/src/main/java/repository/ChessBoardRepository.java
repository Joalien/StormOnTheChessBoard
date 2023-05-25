package repository;

import api.ChessBoardReadService;
import command.Command;

public interface ChessBoardRepository {
    boolean saveCommand(Integer gameId, Command cbs);

    ChessBoardReadService getChessBoardService(Integer gameId);

    boolean undoLastCommand(Integer gameId);
}
