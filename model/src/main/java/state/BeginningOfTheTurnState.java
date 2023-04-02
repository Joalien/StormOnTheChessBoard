package state;

import card.SCCard;

public class BeginningOfTheTurnState implements TurnState {
    @Override
    public boolean tryToMove(ChessBoardFacade chessBoardFacade, String from, String to) {
        boolean hasMoved = chessBoardFacade.getChessBoard().tryToMove(from, to);
        if (hasMoved) chessBoardFacade.setState(StateEnum.MOVE_WITHOUT_CARD_PLAYED);
        return hasMoved;
    }

    @Override
    public boolean tryToPlayCard(ChessBoardFacade chessBoardFacade, SCCard card) {
        boolean hasPlayedCard = card.playOn(chessBoardFacade.getChessBoard());
        if (hasPlayedCard) {
            StateEnum newState = switch (card.getType()) {
                case BEFORE_TURN -> StateEnum.BEFORE_MOVE;
                case REPLACE_TURN -> StateEnum.REPLACE_MOVE;
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
