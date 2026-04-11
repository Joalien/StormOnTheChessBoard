package fr.kubys.dto;

import fr.kubys.core.Color;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayerDto {
    private String name;
    private Color color;
    private List<CardOutputDto> cards;
}
