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
        StateEnum nextState = switch (card.getType()) {
            case BEFORE_TURN -> StateEnum.BEFORE_MOVE;
            case REPLACE_TURN -> StateEnum.END_OF_THE_TURN;
            default -> throw new IllegalStateException("You can only play BEFORE or REPLACE card!");
        };
        boolean hasPlayedCard = card.playOn(chessBoardFacade.getChessBoard());
        if (hasPlayedCard) {
            chessBoardFacade.setState(nextState);
        }
        return hasPlayedCard;
    }

    @Override
    public boolean tryToPass(ChessBoardFacade chessBoardFacade) {
        return false;
    }
}
