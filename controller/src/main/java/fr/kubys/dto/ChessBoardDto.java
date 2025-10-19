package fr.kubys.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
public class ChessBoardDto {
    private Integer id;
    private Set<EffectDto> effects;
    private Map<String, String> pieces;
    private PlayerDto whitePlayer;
    private PlayerDto blackPlayer;
    private Set<CardOutputDto> deck;
    private Set<CardOutputDto> discard;
    private String currentTurn;
}
