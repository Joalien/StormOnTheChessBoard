package fr.kubys.repository;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.command.Command;

public interface ChessBoardRepository {
    boolean saveCommand(Command cbs);

    ChessBoardReadService getChessBoardService(Integer gameId);

    boolean undoLastCommand(Integer gameId);
}
