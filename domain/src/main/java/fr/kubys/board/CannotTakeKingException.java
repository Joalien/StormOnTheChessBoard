package fr.kubys.board;

public class CannotTakeKingException extends RuntimeException {
    public CannotTakeKingException() {
        super("Vous ne pouvez pas capturer le roi adverse !");
    }
}
