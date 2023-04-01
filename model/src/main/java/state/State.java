package state;

import card.SCCard;

public interface State {

    boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to);

    boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card);
}
