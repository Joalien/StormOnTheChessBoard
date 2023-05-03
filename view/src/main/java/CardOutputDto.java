import card.CardType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class CardOutputDto {
    private String name;
    private String description;
    private CardType type;
}
