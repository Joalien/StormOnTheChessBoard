package fr.kubys.matchmaking.controller;

public sealed interface MatchmakingStatus {
    record Waiting(String status) implements MatchmakingStatus {
        public Waiting() {
            this("waiting");
        }
    }

    record Matched(String status, int gameId, String color) implements MatchmakingStatus {
        public Matched(int gameId, String color) {
            this("matched", gameId, color);
        }
    }
}
