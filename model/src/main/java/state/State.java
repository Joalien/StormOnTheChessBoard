package state;

import card.SCCard;

public interface State {

    public abstract boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to);

    public abstract boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card);

    public abstract boolean tryToPass(ChessBoardFacade chessBoardFacade);
}
