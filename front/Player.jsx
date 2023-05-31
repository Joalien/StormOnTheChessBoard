export function Player({player, hiddenCards, showCard}) {
    return (
        <div id={"whitePlayer"}>
            <h3>{player.name}</h3>
            <ul>
                {player.cards.map(card =>
                    <li key={card.name}>
                        {hiddenCards ? "?" : <button onClick={() => showCard(card)}>{card.name}</button>}
                    </li>
                )}
            </ul>
        </div>
    )
}