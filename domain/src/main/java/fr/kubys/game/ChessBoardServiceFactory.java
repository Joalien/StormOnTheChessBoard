package fr.kubys.game;

import fr.kubys.api.ChessBoardService;
import fr.kubys.board.ChessBoard;

import java.util.function.Supplier;

public class ChessBoardServiceFactory {
    public static ChessBoardService newChessBoardService() {
        return new GameStateController();
    }

    public static ChessBoardService newChessBoardService(Supplier<ChessBoard> boardFactory) {
        return new GameStateController(boardFactory);
    }
}
