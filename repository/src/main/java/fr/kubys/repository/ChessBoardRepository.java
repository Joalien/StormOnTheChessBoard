package fr.kubys.repository;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.board.ChessBoard;
import fr.kubys.command.Command;

import java.util.function.Supplier;

public interface ChessBoardRepository {

    Integer createNewGame();

    Integer createCustomGame(Supplier<ChessBoard> boardFactory);

    void saveCommand(Command cbs);

    ChessBoardReadService getChessBoardService(Integer gameId);

    void undoLastCommand(Integer gameId);

    boolean gameExists(Integer gameId);
}
