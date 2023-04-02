package state;

import card.SCCard;

public class BeforeTurnState implements State {
    @Override
    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        boolean hasMoved = chessBoardFacade.getChessBoard().tryToMove(from, to);
        if (hasMoved) chessBoardFacade.setState(StateEnum.SIMPLE_TURN);
        return hasMoved;
    }

    @Override
    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        throw new AlreadyPlayedACardException();
    }

    @Override
    public boolean tryToPass(ChessBoardFacade chessBoardFacade) {
        return false;
    }
}
