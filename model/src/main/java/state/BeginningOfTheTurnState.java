package state;

import card.SCCard;

public class BeginningOfTheTurnState implements TurnState {
    @Override
    public boolean tryToMove(GameStateController gameStateController, String from, String to) {
        boolean hasMoved = gameStateController.getChessBoard().tryToMove(from, to);
        if (hasMoved) gameStateController.setState(StateEnum.MOVE_WITHOUT_CARD_PLAYED);
        return hasMoved;
    }

    @Override
    public boolean tryToPlayCard(GameStateController gameStateController, SCCard card) {
        StateEnum nextState = switch (card.getType()) {
            case BEFORE_TURN -> StateEnum.BEFORE_MOVE;
            case REPLACE_TURN -> StateEnum.END_OF_THE_TURN;
            default -> throw new IllegalStateException("You can only play BEFORE or REPLACE card!");
        };
        boolean hasPlayedCard = card.playOn(gameStateController.getChessBoard());
        if (hasPlayedCard) {
            gameStateController.setState(nextState);
        }
        return hasPlayedCard;
    }

    @Override
    public boolean tryToPass(GameStateController gameStateController) {
        return false;
    }
}
