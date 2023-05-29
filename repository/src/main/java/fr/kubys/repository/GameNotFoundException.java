package fr.kubys.repository;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Integer gameId) {
        super("game %s not found".formatted(gameId));
    }
}
