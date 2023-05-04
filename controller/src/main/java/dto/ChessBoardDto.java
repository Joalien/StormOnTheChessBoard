package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@Builder
@ToString
public class ChessBoardDto {
    private Integer id;
    private Set<EffectDto> effects;
    private Set<PieceDto> pieces;
    private PlayerDto whitePlayer;
    private PlayerDto blackPlayer;
    private Set<CardOutputDto> deck;
}
