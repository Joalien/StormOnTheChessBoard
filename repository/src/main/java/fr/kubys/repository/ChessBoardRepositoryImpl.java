package fr.kubys.repository;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.api.ChessBoardService;
import fr.kubys.api.ChessBoardWriteService;
import fr.kubys.command.Command;
import fr.kubys.command.StartGameCommand;
import fr.kubys.game.ChessBoardServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ChessBoardRepositoryImpl implements ChessBoardRepository {
    private final Map<Integer, List<Command>> store = new HashMap<>();

    @Override
    public Integer createNewGame() {
        Integer nextGameId = store.keySet().stream()
                .mapToInt(value -> value)
                .max()
                .orElse(0) + 1;
        store.put(nextGameId, new LinkedList<>());
        saveCommand(StartGameCommand.builder().gameId(nextGameId).build());
        return nextGameId;
    }

    @Override
    public void saveCommand(Command command) {
        ChessBoardWriteService chessBoardWriteService = computeChessBoard(command.getGameId());

        command.execute(chessBoardWriteService);
        log.info("[Chessboard {}] {}", command.getGameId(), command);
        store.get(command.getGameId()).add(command);
    }

    @Override
    public ChessBoardReadService getChessBoardService(Integer gameId) {
        return computeChessBoard(gameId);
    }

    @Override
    public void undoLastCommand(Integer gameId) {
        throw new UnsupportedOperationException("Not implemented yet but easy to do ;)");
    }

    @Override
    public boolean gameExists(Integer gameId) {
        return store.containsKey(gameId);
    }

    private ChessBoardService computeChessBoard(Integer gameId) {
        if (!store.containsKey(gameId)) throw new GameNotFoundException(gameId);
        ChessBoardService gameStateController = ChessBoardServiceFactory.newChessBoardService();
        store.get(gameId).forEach(command -> command.execute(gameStateController));
        return gameStateController;
    }
}