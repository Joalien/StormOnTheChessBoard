import api.ChessBoardReadService;
import card.Card;
import command.StartGameCommand;
import dto.*;
import effet.Effect;
import piece.Piece;
import player.Player;

import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        ChessBoardRepository chessBoardRepository = new ChessBoardRepositoryImpl();
        chessBoardRepository.saveCommand(1, StartGameCommand.builder().gameId(1).build());
        ChessBoardReadService chessBoard = chessBoardRepository.getChessBoardService(1);
        ChessBoardDto chessBoardDto = ChessBoardDto.builder()
                .id(1)
                .effects(chessBoard.getEffects().stream().map(Main::map).collect(Collectors.toSet()))
                .deck(chessBoard.getCards().stream().map(Main::map).collect(Collectors.toSet()))
                .whitePlayer(map(chessBoard.getWhite()))
                .blackPlayer(map(chessBoard.getBlack()))
                .pieces(chessBoard.getPieces().stream().map(Main::map).collect(Collectors.toSet()))
                .build();
        System.out.println(chessBoardDto.toString());
    }

    private static PieceDto map(Piece p) {
        return PieceDto.builder()
                .name(p.toString())
                .color(p.getColor())
                .position(p.getPosition())
                .build();
    }

    private static PlayerDto map(Player p) {
        return PlayerDto.builder()
                .name(p.getName())
                .color(p.getColor())
                .build();
    }

    private static CardOutputDto map(Card c) {
        return CardOutputDto.builder()
                .name(c.getName())
//                .description(c.getDescription())
                .type(c.getType())
                .build();
    }

    private static EffectDto map(Effect c) {
        return EffectDto.builder()
                .name(c.getName())
                .build();
    }
}