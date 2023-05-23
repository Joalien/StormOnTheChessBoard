package state.exception;

public class AlreadyMovedException extends IllegalStateException {
    public AlreadyMovedException() {
        super("You have already moved!");
    }
}