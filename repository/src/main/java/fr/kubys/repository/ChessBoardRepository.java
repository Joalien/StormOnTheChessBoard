package fr.kubys.repository;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.command.Command;

public interface ChessBoardRepository {

    Integer createNewGame();

    void saveCommand(Command cbs);

    ChessBoardReadService getChessBoardService(Integer gameId);

    void undoLastCommand(Integer gameId);

    boolean gameExists(Integer gameId);
}
