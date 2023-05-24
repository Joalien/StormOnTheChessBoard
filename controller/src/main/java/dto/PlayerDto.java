package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import core.Color;

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
