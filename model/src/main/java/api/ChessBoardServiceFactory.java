package api;

import state.GameStateController;

public class ChessBoardServiceFactory {
    private static final GameStateController chessBoardService = new GameStateController();
    public static ChessBoardService getChessBoardCommand() {
        return chessBoardService;
    }
    public static ExposeGetters getChessBoardRead() {
        return chessBoardService;
    }
}
