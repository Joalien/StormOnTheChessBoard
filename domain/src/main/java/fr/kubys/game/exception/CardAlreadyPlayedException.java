package fr.kubys.game.exception;

public class CardAlreadyPlayedException extends IllegalStateException {
    public CardAlreadyPlayedException() {
        super("Vous avez déjà joué une carte !");
    }
}
