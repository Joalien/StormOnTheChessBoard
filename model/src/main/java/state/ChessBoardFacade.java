package state;

import board.ChessBoard;
import card.*;
import lombok.Getter;
import lombok.Setter;
import piece.Color;
import player.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

@Getter
public class ChessBoardFacade {

    public static final int NUMBER_OF_CARDS_IN_HAND = 5;
    private ChessBoard chessBoard;
    private Player white;
    private Player black;
    private List<Class<? extends SCCard>> cards;
    private Color currentMove;
    @Setter
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
        currentMove = Color.WHITE;
        state = StateEnum.BEGINNING_OF_THE_TURN;
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
}
