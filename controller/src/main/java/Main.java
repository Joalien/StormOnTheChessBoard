import api.ChessBoardReadService;
import card.Card;
import command.PlayMoveCommand;
import command.StartGameCommand;
import dto.*;
import board.effect.Effect;
import piece.Piece;
import player.Player;

import java.util.stream.Collectors;

import static core.Position.e2;
import static core.Position.e4;

public class Main {
    public static void main(String[] args) {
        ChessBoardRepository chessBoardRepository = new ChessBoardRepositoryImpl();

        Integer gameId = 1;
        chessBoardRepository.saveCommand(gameId, StartGameCommand.builder().gameId(gameId).build());
        System.out.println(mapToDto(gameId, chessBoardRepository.getChessBoardService(gameId)).toString());

        chessBoardRepository.saveCommand(gameId, PlayMoveCommand.builder()
                .gameId(gameId)
                .from(e2)
                .to(e4).build());
        System.out.println(mapToDto(gameId, chessBoardRepository.getChessBoardService(gameId)).toString());
    }

    private static ChessBoardDto mapToDto(Integer gameId, ChessBoardReadService chessBoard) {
        return ChessBoardDto.builder()
                .id(gameId)
                .effects(chessBoard.getEffects().stream().map(Main::map).collect(Collectors.toSet()))
                .deck(chessBoard.getCards().stream().map(Main::map).collect(Collectors.toSet()))
                .whitePlayer(map(chessBoard.getWhite()))
                .blackPlayer(map(chessBoard.getBlack()))
                .pieces(chessBoard.getPieces().stream().map(Main::map).collect(Collectors.toSet()))
                .build();
    }

    private static EffectDto map(Effect c) {
        return EffectDto.builder()
                .name(c.getName())
                .build();
    }

    private static CardOutputDto map(Card c) {
        return CardOutputDto.builder()
                .name(c.getName())
//                .description(c.getDescription())
                .type(c.getType())
                .build();
    }

    private static PlayerDto map(Player p) {
        return PlayerDto.builder()
                .name(p.getName())
                .color(p.getColor())
                .build();
    }

    private static PieceDto map(Piece p) {
        return PieceDto.builder()
                .name(p.toString())
                .color(p.getColor())
                .position(p.getPosition())
                .build();
    }
}