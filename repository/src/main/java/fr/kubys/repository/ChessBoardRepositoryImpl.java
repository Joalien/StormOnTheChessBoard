package fr.kubys.repository;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.api.ChessBoardService;
import fr.kubys.api.ChessBoardWriteService;
import fr.kubys.command.Command;
import fr.kubys.command.StartGameCommand;
import fr.kubys.game.ChessBoardServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

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
        saveCommand(StartGameCommand.builder().gameId(nextGameId).build());
        return nextGameId;
    }

    @Override
    public void saveCommand(Command command) {
        ChessBoardWriteService chessBoardWriteService = computeChessBoard(command.getGameId());

        command.execute(chessBoardWriteService);
        store.putIfAbsent(command.getGameId(), new LinkedList<>());
        log.info("[Chessboard {}] {}", command.getGameId(), command);
        store.get(command.getGameId()).add(command);
    }

    @Override
    public ChessBoardReadService getChessBoardService(Integer gameId) {
        if (!doesGameExist(gameId)) throw new GameNotFoundException(gameId);

        return computeChessBoard(gameId);
    }

    @Override
    public void undoLastCommand(Integer gameId) {
        throw new UnsupportedOperationException("Not implemented yet but easy to do ;)");
    }

    @Override
    public boolean doesGameExist(Integer gameId) {
        return store.containsKey(gameId);
    }

    private ChessBoardService computeChessBoard(Integer gameId) {
        ChessBoardService gameStateController = ChessBoardServiceFactory.newChessBoardService();
        store.getOrDefault(gameId, Collections.emptyList())
                .forEach(command -> command.execute(gameStateController));
        return gameStateController;
    }
}