package fr.kubys.StormOnTheChessBoard.dto;

import fr.kubys.core.Color;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@Builder
@ToString
public class PlayerDto {
    private String name;
    private Color color;
    private Set<Class<CardOutputDto>> cards;
}
