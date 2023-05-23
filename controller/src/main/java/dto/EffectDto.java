package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import position.Position;

import java.util.Set;

@Getter
@Setter
@Builder
@ToString
public class EffectDto {
    private String name;
    private Set<Position> positions;
}
