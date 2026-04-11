package fr.kubys.game.exception;

public class AlreadyMovedException extends IllegalStateException {
    public AlreadyMovedException() {
        super("Vous avez déjà joué votre coup !");
    }
}
