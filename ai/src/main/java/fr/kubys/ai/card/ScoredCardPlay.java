package fr.kubys.ai.card;

import fr.kubys.card.Card;
import fr.kubys.card.params.CardParam;

public record ScoredCardPlay(Card<? extends CardParam> card, CardParam param, int score) {
}
