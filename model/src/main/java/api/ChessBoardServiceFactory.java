package api;

import state.GameStateController;

public class ChessBoardServiceFactory {
//    private static final GameStateController chessBoardService = new GameStateController();
    public static ChessBoardService newChessBoardService() {
        return new GameStateController();
    }
}
