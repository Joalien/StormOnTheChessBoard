package fr.kubys.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@Builder
@ToString
public class HistoryEntryDto {
    private final int index;
    private final String color;
    private final String action;
    private final String instant;
    /** English class name (e.g. "BombingCard") if this entry is a card play, else null. */
    private final String cardEnglishName;
    /** Display name in the game's language if this entry is a card play, else null. */
    private final String cardName;
    /** Card description if this entry is a card play, else null. */
    private final String cardDescription;
    /** Card timing window (BEFORE_TURN, AFTER_TURN, REPLACE_TURN) if this entry is a card play, else null. */
    private final String cardType;
    /** Parameters used by the player who played the card, in the same format as PlayCardWithImmutableParamCommand. */
    private final Map<String, Object> cardParams;
}
