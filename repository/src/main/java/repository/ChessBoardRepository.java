package repository;

import api.ChessBoardReadService;
import command.Command;

public interface ChessBoardRepository {
    boolean saveCommand(Command cbs);

    ChessBoardReadService getChessBoardService(Integer gameId);

    boolean undoLastCommand(Integer gameId);
}
