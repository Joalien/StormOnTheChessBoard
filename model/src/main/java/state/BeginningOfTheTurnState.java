package state;

import card.SCCard;

public class BeginningOfTheTurnState implements State {
    @Override
    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        boolean hasMoved = chessBoardFacade.getChessBoard().tryToMove(from, to);
        if (hasMoved) chessBoardFacade.setState(StateEnum.SIMPLE_TURN);
        return hasMoved;
    }

    @Override
    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        boolean hasPlayedCard = card.playOn(chessBoardFacade.getChessBoard());
        if (hasPlayedCard) {
            StateEnum newState = switch (card.getType()) {
                case BEFORE_TURN -> StateEnum.BEFORE_TURN;
                case REPLACE_TURN -> StateEnum.REPLACE_TURN;
                default -> throw new IllegalStateException("You can only play BEFORE or REPLACE card!");
            };
            chessBoardFacade.setState(newState);
        }
        return hasPlayedCard;
    }

    @Override
    public boolean tryToPass(ChessBoardFacade chessBoardFacade) {
        return false;
    }
}
