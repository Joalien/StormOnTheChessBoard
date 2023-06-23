export function CardParameters({card, selectedParam, setSelectedParam, playCardCallback}) {
    return (
        <div id={"selectedCard"} style={{'backgroundColor': 'gray'}}>
            <h3>{card.name}</h3>
            <p>({card.type})</p>
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
            <button onClick={playCardCallback} disabled={Object.values(card.param).some(x => x === null)}>Jouer la carte</button>
        </div>
    )
}