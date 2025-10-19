import {Card} from "./Card";

export function Player({player, hiddenCards, showCard}) {
    return (
        <div id={"whitePlayer"}>
            <div style={{display: 'flex', justifyContent: 'space-between'}}>
                {player.cards.map(card =>
                        <Card hidden={hiddenCards}
                              name={card.englishName}
                              showCard={() => showCard(card)}
                        />
                )}
            </div>
        </div>
    )
}