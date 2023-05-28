import {useEffect, useState} from "react";

const base = "http://localhost:9000/chessboard/";

export function Player({player, hiddenCards}) {
    return (
        <div id={"whitePlayer"}>
            <h3>{player.name}</h3>
            <ul>
                {player.cards.map(card => <li key={card.name}>{hiddenCards ? "?" : card.name}</li>)}
            </ul>
        </div>
    )
}