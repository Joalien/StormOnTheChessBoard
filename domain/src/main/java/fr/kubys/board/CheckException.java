package fr.kubys.board;

public class CheckException extends RuntimeException {
    public CheckException() {
        super("Impossible de passer le tour : votre roi est en échec !");
    }
}
