import api.ChessBoardReadService;
import api.ChessBoardService;
import api.ChessBoardServiceFactory;
import command.Command;

import java.util.*;

public class ChessBoardRepositoryImpl implements ChessBoardRepository {
    Map<Integer, List<Command>> store = new HashMap<>();

    @Override
    public boolean saveCommand(Integer gameId, Command cbs) {
        ChessBoardService gameStateController = computeChessBoard(gameId);

        if (cbs.execute(gameStateController)) { // TODO simplify me
            store.putIfAbsent(gameId, new LinkedList<>());
            List<Command> commands = store.get(gameId);
            commands.add(cbs);
            store.put(gameId, commands);
            return true;
        } else return false;
    }

    @Override
    public ChessBoardReadService getChessBoardService(Integer gameId) {
        return computeChessBoard(gameId);
    }

    private ChessBoardService computeChessBoard(Integer gameId) {
        ChessBoardService gameStateController = ChessBoardServiceFactory.newChessBoardService();
        store.getOrDefault(gameId, Collections.emptyList())
                .forEach(command -> command.execute(gameStateController));
        return gameStateController;
    }
}