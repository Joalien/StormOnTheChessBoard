package state;

public class AlreadyPlayedACardException extends IllegalStateException{
    public AlreadyPlayedACardException() {
        super("You have already played a card!");
    }
}
