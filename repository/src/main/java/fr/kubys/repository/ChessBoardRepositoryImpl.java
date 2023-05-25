package fr.kubys.repository;

import fr.kubys.api.ChessBoardReadService;
import fr.kubys.api.ChessBoardService;
import fr.kubys.command.Command;
import fr.kubys.game.ChessBoardServiceFactory;
import fr.kubys.api.ChessBoardWriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class ChessBoardRepositoryImpl implements ChessBoardRepository {
    private final Map<Integer, List<Command>> store = new HashMap<>();

    @Override
    public void saveCommand(Command command) {
        ChessBoardWriteService chessBoardWriteService = computeChessBoard(command.getGameId());

        command.execute(chessBoardWriteService);
        store.putIfAbsent(command.getGameId(), new LinkedList<>());
        log.info("{} has been played on chessboard {}", command, command.getGameId());
        store.get(command.getGameId()).add(command);
    }

    @Override
    public ChessBoardReadService getChessBoardService(Integer gameId) {
        if (!store.containsKey(gameId)) throw new IllegalArgumentException("game %s not found".formatted(gameId));

        return computeChessBoard(gameId);
    }

    @Override
    public void undoLastCommand(Integer gameId) {
        throw new UnsupportedOperationException("Not implemented yet but easy to do ;)");
    }

    private ChessBoardService computeChessBoard(Integer gameId) {
        ChessBoardService gameStateController = ChessBoardServiceFactory.newChessBoardService();
        store.getOrDefault(gameId, Collections.emptyList())
                .forEach(command -> command.execute(gameStateController));
        return gameStateController;
    }
}