package fr.kubys.matchmaking.model;

public class MatchResult {
    private final String whiteToken;
    private final String blackToken;
    private Integer gameId;

    MatchResult(String whiteToken, String blackToken) {
        this.whiteToken = whiteToken;
        this.blackToken = blackToken;
    }

    public String getColorForToken(String token) {
        if (token.equals(whiteToken)) return "white";
        if (token.equals(blackToken)) return "black";
        throw new IllegalArgumentException("Token not part of this match: " + token);
    }

    public String getWhiteToken() {
        return whiteToken;
    }

    public String getBlackToken() {
        return blackToken;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }
}
