package fr.kubys.controller;

public sealed interface JoinResponse {
    record Waiting(String token, String status) implements JoinResponse {
        public Waiting(String token) {
            this(token, "waiting");
        }
    }

    record Matched(String token, String status, int gameId, String color) implements JoinResponse {
        public Matched(String token, int gameId, String color) {
            this(token, "matched", gameId, color);
        }
    }
}
