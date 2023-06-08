package fr.kubys.game.exception;

public class CardAlreadyPlayedException extends IllegalStateException {
    public CardAlreadyPlayedException() {
        super("You have already played a card!");
    }
}
