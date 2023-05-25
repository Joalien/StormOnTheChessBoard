package fr.kubys.game;

import fr.kubys.api.ChessBoardService;
import fr.kubys.board.ChessBoard;
import fr.kubys.board.effect.Effect;
import fr.kubys.card.*;
import fr.kubys.piece.Square;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import fr.kubys.core.Color;
import fr.kubys.piece.Piece;
import fr.kubys.player.Player;
import fr.kubys.core.Position;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter(AccessLevel.PACKAGE)
@Slf4j
public class GameStateController implements ChessBoardService {

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
        if (this.chessBoard != null)
            throw new IllegalStateException("Game has already started!");

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
    public void tryToMove(Position from, Position to) {
        assertGameHasAlreadyStarted();
        Optional<Piece> pieceToMove = chessBoard.at(from).getPiece();
        if (pieceToMove.isEmpty()) throw new IllegalArgumentException("There is no piece on %s".formatted(from));
        if (pieceToMove.get().getColor() != currentPlayer.getColor())
            throw new IllegalStateException("%s player cannot move %s piece".formatted(currentPlayer.getColor(), pieceToMove.get().getColor()));

        currentState.getState().tryToMove(this, from, to);
    }

    @Override
    public void tryToPlayCard(Card card, List<?> params) {
        assertGameHasAlreadyStarted();
        if (!currentPlayer.getCards().contains(card))
            throw new CardNotFoundException("Player %s does not have %s in hand!".formatted(currentPlayer, card));

        card.setIsPlayedBy(currentPlayer.getColor());
        currentState.getState().tryToPlayCard(this, card, params);
        currentPlayer.getCards().remove(card);
        dealCard(currentPlayer);
    }

    @Override
    public void tryToPass() {
        assertGameHasAlreadyStarted();
        currentState.getState().tryToPass(this);
        setCurrentState(StateEnum.BEGINNING_OF_THE_TURN);
        swapCurrentPlayer();
    }

    private void assertGameHasAlreadyStarted() {
        if (chessBoard == null) throw new IllegalStateException("Game has not started yet!");
    }

    private void swapCurrentPlayer() {
        if (getCurrentPlayer() == getWhite()) setCurrentPlayer(getBlack());
        else if (getCurrentPlayer() == getBlack()) setCurrentPlayer(getWhite());
        else throw new IllegalStateException("Who's turn?");
    }

    @Override
    public Set<Piece> getPieces() {
        return Stream.of(chessBoard.allyPieces(Color.WHITE).stream(), chessBoard.allyPieces(Color.BLACK).stream())
                .flatMap(x -> x)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Effect> getEffects() {
        return chessBoard.getEffects();
    }

    void setCurrentState(StateEnum currentState) {
        log.debug("{} is now in state {}", this.currentPlayer, currentState);
        this.currentState = currentState;
    }
}
