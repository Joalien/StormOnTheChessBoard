package fr.kubys.game.exception;

public class InvalidGameActionException extends IllegalStateException {
    public InvalidGameActionException(String message) {
        super(message);
    }
}
