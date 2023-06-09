package fr.kubys.command;

import fr.kubys.api.ChessBoardService;
import fr.kubys.card.Card;
import fr.kubys.card.CardNotFoundException;
import fr.kubys.card.params.CardParam;
import fr.kubys.mapper.CardParametersMapper;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Objects;

@SuperBuilder
@Getter
public final class PlayCardWithImmutableParamCommand<T extends CardParam> extends Command {
    String cardName;
    Map<String, Object> param;

    @Override
    public void execute(ChessBoardService chessBoardWriteService) {
        Card<T> card = chessBoardWriteService.getCurrentPlayer().getCards().stream()
                .filter(c -> Objects.equals(c.getName(), cardName))
                .findFirst()
                .map(CardParametersMapper::<T>checkThatCardParametersMatch)
                .orElseThrow(() -> new CardNotFoundException("Card %s is not in user hand!".formatted(cardName)));
        T parameters = CardParametersMapper.mapParamToCardParam(param, card.getClazz(), chessBoardWriteService);

        PlayCardWithMutableParam.<T>builder()
                .card(card)
                .parameters(parameters)
                .build()
                .execute(chessBoardWriteService);
    }

    @Override
    public String toString() {
        return "Play card %s with parameters %s".formatted(cardName, param);
    }
}
