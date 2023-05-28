export function Player({player}) {
    return (
        <div id={"whitePlayer"}>
            <h3>{player.name}</h3>
            <ul>
                {player.cards.map(card => <li key={card.name}>{card.name}</li>)}
            </ul>
        </div>
    )
}