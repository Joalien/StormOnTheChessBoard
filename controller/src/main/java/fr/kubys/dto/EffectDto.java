package fr.kubys.dto;

import fr.kubys.core.Position;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class EffectDto {
    private String name;
    private List<Position> positions;
    private String cardName;
    private String cardEnglishName;
    private String cardDescription;
}
