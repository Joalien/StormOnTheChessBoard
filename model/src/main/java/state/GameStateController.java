package state;

import api.ChessBoardService;
import api.ExposeGetters;
import board.ChessBoard;
import card.*;
import effet.Effect;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import piece.Color;
import piece.Piece;
import player.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter(AccessLevel.PACKAGE)
@Slf4j
public class GameStateController implements ChessBoardService, ExposeGetters {

    private static final int NUMBER_OF_CARDS_IN_HAND = 4;
    private ChessBoard chessBoard;
    @Getter
    private Player white;
    @Getter
    private Player black;
    @Getter
    private List<Card> cards;
    @Setter(AccessLevel.PACKAGE)
    private Player currentPlayer;
    private StateEnum currentState;

    public GameStateController() {
    }

    @Override
    public void startGame() {
        this.chessBoard = ChessBoard.createWithInitialState();
        white = new Player("Name1", Color.WHITE);
        black = new Player("Name2", Color.BLACK);

        initDeck();
        IntStream.range(0, NUMBER_OF_CARDS_IN_HAND)
                .peek(x -> dealCard(white))
                .forEach(x -> dealCard(black));
        currentPlayer = white;
        currentState = StateEnum.BEGINNING_OF_THE_TURN;
    }

    private void initDeck() {
        cards = new LinkedList<>();
        cards.add(new BlackHoleCard());
        cards.add(new BombingCard());
        cards.add(new ChargeCard());
        cards.add(new CourtlyLoveCard());
        cards.add(new HomeCard());
        cards.add(new KangarooCard());
        cards.add(new LightweightSquadCard());
        cards.add(new MagnetismCard());
        cards.add(new QuadrilleCard());
        cards.add(new ReflectedBishopCard());
        cards.add(new StableCard());

        Collections.shuffle(cards);
    }

    private void dealCard(Player player) {
        player.getCards().add(cards.remove(0));
    }

    @Override
    public boolean tryToMove(String from, String to) {
        return currentState.getState().tryToMove(this, from, to);
    }

    @Override
    public boolean tryToPlayCard(Card card, List<?> params) {
        card.setIsPlayedBy(currentPlayer.getColor());
        boolean isPlayed = currentState.getState().tryToPlayCard(this, card, params);
        if (isPlayed) {
            currentPlayer.getCards().remove(card);
            dealCard(currentPlayer);
        }
        return isPlayed;
    }

    @Override
    public boolean tryToPass() {
        return currentState.getState().tryToPass(this);
    }

    void setCurrentState(StateEnum currentState) {
        log.debug("{} is now in state {}", this.currentPlayer, currentState);
        this.currentState = currentState;
    }

    @Override
    public Set<Piece> getPieces() {
        return Stream.of(
                chessBoard.allyPieces(Color.WHITE).stream(),
                chessBoard.allyPieces(Color.BLACK).stream()
        ).flatMap(x -> x)
        .collect(Collectors.toSet());
    }

    @Override
    public Set<Effect> getEffects() {
        return chessBoard.getEffects();
    }
}
