package state;

import board.ChessBoard;
import card.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import piece.Color;
import player.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@Slf4j
public class ChessBoardFacade {

    public static final int NUMBER_OF_CARDS_IN_HAND = 5;
    private ChessBoard chessBoard;
    private Player white;
    private Player black;
    private List<Class<? extends SCCard>> cards;
    @Setter(AccessLevel.PACKAGE)
    private Player currentPlayer;
    private StateEnum state;

    public ChessBoardFacade() {
    }

    public void startGame() {
        this.chessBoard = ChessBoard.createWithInitialState();
        white = new Player("Name1", Color.WHITE);
        black = new Player("Name2", Color.BLACK);

        initDeck();
        IntStream.range(0, NUMBER_OF_CARDS_IN_HAND)
                .peek(x -> dealCard(white))
                .forEach(x -> dealCard(black));
        currentPlayer = white;
        state = StateEnum.BEGINNING_OF_THE_TURN;
    }

    private void initDeck() {
        cards = new LinkedList<>();
        cards.add(BlackHoleCard.class);
        cards.add(BombingCard.class);
        cards.add(ChargeCard.class);
        cards.add(CourtlyLoveCard.class);
        cards.add(HomeCard.class);
        cards.add(KangarooCard.class);
        cards.add(LightweightSquadCard.class);
        cards.add(MagnetismCard.class);
        cards.add(QuadrilleCard.class);
        cards.add(ReflectedBishopCard.class);
        cards.add(StableCard.class);

        Collections.shuffle(cards);
    }

    private void dealCard(Player player) {
        player.getCards().add(cards.remove(0));
    }

    public boolean tryToMove(String from, String to) {
        return state.tryToMove(this, from, to);
    }

    public boolean tryToPlayCard(SCCard card) {
        return state.tryToPlayCard(this, card);
    }

    public boolean tryToPass() {
        return state.tryToPass(this);
    }

    void setState(StateEnum state) {
        log.debug("{} is in state {}", this.currentPlayer, state);
        this.state = state;
    }
}
