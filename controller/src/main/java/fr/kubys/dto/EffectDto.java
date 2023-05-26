package fr.kubys.dto;

import fr.kubys.core.Position;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@Builder
@ToString
public class EffectDto {
    private String name;
    private Set<Position> positions;
}
