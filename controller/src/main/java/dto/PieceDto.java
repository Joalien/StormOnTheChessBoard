package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import core.Color;
import core.Position;

@Getter
@Setter
@Builder
@ToString
public class PieceDto {
    private String name;
    private Color color;
    private Position position;

}
