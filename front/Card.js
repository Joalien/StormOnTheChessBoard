import {useState} from "react";

export function Card({card}) {

    const [selectedParam, setSelectedParam] = useState(null)
    function playCard() {

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
                            {key} : <span style={{color: card.param[key] === "choose a value" ? "red" : "blue"}}>{card.param[key]}</span>
                        </label>
                    </li>
                )}
            </ul>
            <button onClick={playCard}>Jouer la carte</button>
        </div>
    )
}