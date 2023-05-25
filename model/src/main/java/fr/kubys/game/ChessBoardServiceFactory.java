package fr.kubys.game;

import fr.kubys.api.ChessBoardService;

public class ChessBoardServiceFactory {
    //    private static final GameStateController chessBoardService = new GameStateController();
    public static ChessBoardService newChessBoardService() {
        return new GameStateController();
    }
}
