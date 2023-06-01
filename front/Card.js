import {useEffect, useState} from "react";

export function Card({card, selectedParam, setSelectedParam}) {

    function playCard() {
        // REST CALL
        // MOVE up
    }

    return (
        <div id={"selectedCard"} style={{'backgroundColor': 'gray'}}>
            <h3>{card.name}</h3>
            <p>{card.description}</p>
            <ul>
                {Object.keys(card.param).map((key, i) =>
                    <li key={i}>
                        <label>
                            <input
                                type="radio"
                                name={key}
                                value={card.param[key]}
                                checked={selectedParam === key}
                                onChange={() => setSelectedParam(key)}
                                className="form-check-input"
                            />
                            {key} : <span style={{color: card.param[key] === null ? "red" : "blue"}}>{card.param[key] === null ? "Choose a value" : card.param[key]}</span>
                        </label>
                    </li>
                )}
            </ul>
            <button onClick={playCard} disabled={Object.values(card.param).some(x => x === null)}>Jouer la carte</button>
        </div>
    )
}