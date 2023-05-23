package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import piece.Color;
import position.Position;

@Getter
@Setter
@Builder
@ToString
public class PieceDto {
    private String name;
    private Color color;
    private Position position;

}
