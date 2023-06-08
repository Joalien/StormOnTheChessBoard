package fr.kubys.dto;

import fr.kubys.card.CardType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@Builder
@ToString
public class CardOutputDto {
    private Integer id;
    private String name;
    private String description;
    private CardType type;
    private Map<String, Object> param;
}
