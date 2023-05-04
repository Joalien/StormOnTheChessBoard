import command.PlayMoveCommand;
import command.StartGameCommand;


public class ChessBoardAggregate {
    ChessBoardRepository chessBoardEventStore;

    public ChessBoardAggregate(ChessBoardRepository chessBoardEventStore) {
        this.chessBoardEventStore = chessBoardEventStore;
    }

    public static void main(String[] args) {
        ChessBoardAggregate chessBoardAggregate = new ChessBoardAggregate(new ChessBoardRepositoryImpl());
        Integer gameId = 1;

        chessBoardAggregate.chessBoardEventStore.saveCommand(gameId, StartGameCommand.builder()
                .gameId(gameId)
                .build());
        chessBoardAggregate.chessBoardEventStore.saveCommand(gameId, PlayMoveCommand.builder()
                .gameId(gameId)
                .from("e2")
                .to("e4").build());
        System.out.println();

    }

//    public static boolean applyEvent(command.Command e) {
//        if (e instanceof command.StartGameCommand) tryToStartGame((command.StartGameCommand) e);
//        else if (e instanceof command.PlayMoveCommand)  tryToMove((command.PlayMoveCommand) e);
//        throw new IllegalStateException();
//    }
//    private boolean tryToStartGame(command.StartGameCommand c) {
//        ChessBoardWriteService cbs = chessBoardEventStore.getChessBoardService(c.gameId);
//        boolean b = cbs.startGame();
//        if (b) chessBoardEventStore.saveCommand(c.gameId, cbs);
//        return b;
//    }
//
//    private boolean tryToMove(command.PlayMoveCommand c) {
//        ChessBoardWriteService cbs = chessBoardEventStore.getChessBoardService(c.gameId);
//        boolean b = cbs
//                .tryToMove(c.from, c.to);
//        if (b) chessBoardEventStore.saveCommand(c.gameId, cbs);
//        return b;
//    }
}