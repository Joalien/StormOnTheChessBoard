package state;

import card.SCCard;

public interface TurnState {

    boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to);

    boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card);

    boolean tryToPass(ChessBoardFacade chessBoardFacade);
}
