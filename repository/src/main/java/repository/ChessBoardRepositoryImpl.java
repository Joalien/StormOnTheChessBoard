package repository;

import api.ChessBoardReadService;
import api.ChessBoardService;
import game.ChessBoardServiceFactory;
import api.ChessBoardWriteService;
import command.Command;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ChessBoardRepositoryImpl implements ChessBoardRepository {
    private final Map<Integer, List<Command>> store = new HashMap<>();

    @Override
    public boolean saveCommand(Command cbs) {
        ChessBoardWriteService chessBoardWriteService = computeChessBoard(cbs.getGameId());

        if (cbs.execute(chessBoardWriteService)) {
            store.putIfAbsent(cbs.getGameId(), new LinkedList<>());
            log.info("{} has been played on chessboard {}", cbs, cbs.getGameId());
            return store.get(cbs.getGameId()).add(cbs);
        } else return false;
    }

    @Override
    public ChessBoardReadService getChessBoardService(Integer gameId) {
        if (!store.containsKey(gameId)) throw new IllegalArgumentException("game %s not found".formatted(gameId));

        return computeChessBoard(gameId);
    }

    @Override
    public boolean undoLastCommand(Integer gameId) {
        throw new UnsupportedOperationException("Not implemented yet but easy to do ;)");
    }

    private ChessBoardService computeChessBoard(Integer gameId) {
        ChessBoardService gameStateController = ChessBoardServiceFactory.newChessBoardService();
        store.getOrDefault(gameId, Collections.emptyList())
                .forEach(command -> command.execute(gameStateController));
        return gameStateController;
    }
}