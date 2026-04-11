import {Image} from "react-native";
import cardImages from "./cardImages";

const TYPE_LABELS = {
    BEFORE_TURN:  'Before Move',
    AFTER_TURN:   'After Move',
    REPLACE_TURN: 'Replaces Move',
    ENEMY_TURN:   "Enemy's Turn",
};

function isBarricadeCard(card) {
    return card && card.englishName === 'BarricadeCard';
}

function BarricadeParams({card, selectedParam, setSelectedParam, barricadeEdges, setBarricadeEdges}) {
    const edgeDefs = [
        {label: 'Edge 1', index: 0, fromKey: 'from1', toKey: 'to1'},
        {label: 'Edge 2', index: 1, fromKey: 'from2', toKey: 'to2'},
    ];

    return (<>{edgeDefs.map(({label, index, fromKey, toKey}) => {
        const edge = barricadeEdges[index];
        const isComplete = !!edge;
        const isActive = !isComplete && barricadeEdges.length === index;
        const display = isComplete ? `${edge[0]} | ${edge[1]}` : null;

        return (
            <div
                key={index}
                className={`param-row${isActive ? ' active' : ''}`}
                onClick={() => {
                    if (isComplete) {
                        // Reset this edge and all after it
                        const newEdges = barricadeEdges.slice(0, index);
                        setBarricadeEdges(newEdges);
                        const newParam = {...card.param};
                        newParam[fromKey] = null;
                        newParam[toKey] = null;
                        if (index === 0) { newParam.from2 = null; newParam.to2 = null; }
                        setSelectedParam(fromKey);
                    }
                }}
                style={isComplete ? {cursor: 'pointer'} : {cursor: 'default'}}
            >
                <div style={{
                    width: '8px', height: '8px', borderRadius: '50%', flexShrink: 0,
                    background: isActive ? '#d4a843' : isComplete ? '#3fb950' : '#484f58',
                    boxShadow: isActive ? '0 0 8px rgba(212,168,67,0.5)' : 'none',
                }}/>
                <div style={{flex: 1, minWidth: 0}}>
                    <div style={{fontSize: '12px', fontWeight: '600', color: '#c9d1d9', marginBottom: '3px'}}>
                        {label}
                    </div>
                    <div style={{
                        fontSize: '11px',
                        color: isComplete ? '#d4a843' : isActive ? '#8b949e' : '#484f58',
                        fontFamily: 'monospace',
                        letterSpacing: '0.3px',
                    }}>
                        {display || (isActive ? 'click an edge on the board...' : '—')}
                    </div>
                </div>
            </div>
        );
    })}</>);
}

export function CardParameters({card, selectedParam, setSelectedParam, playCardCallback, barricadeEdges, setBarricadeEdges, isPlayable = true}) {
    const hasImage = card.englishName in cardImages;
    const paramKeys = Object.keys(card.param || {});
    const allParamsSet = !Object.values(card.param || {}).some(x => x === null);
    const typeLabel = TYPE_LABELS[card.type] || card.type;
    const barricade = isBarricadeCard(card);

    return (
        <div className="sotc-panel" style={{padding: '20px', display: 'flex', flexDirection: 'column', gap: '16px'}}>
            {/* Card image */}
            {hasImage && (
                <div style={{display: 'flex', justifyContent: 'center'}}>
                    <div style={{
                        borderRadius: '10px',
                        overflow: 'hidden',
                        boxShadow: '0 8px 28px rgba(0,0,0,0.55)',
                        display: 'inline-block',
                        lineHeight: 0,
                    }}>
                        <Image source={cardImages[card.englishName]} style={{width: 126, height: 180}}/>
                    </div>
                </div>
            )}

            {/* Name & type */}
            <div style={{textAlign: 'center', display: 'flex', flexDirection: 'column', gap: '8px', alignItems: 'center'}}>
                <h3 style={{fontSize: '15px', fontWeight: '700', color: '#e6edf3', margin: 0, lineHeight: 1.3}}>
                    {card.name || card.englishName}
                </h3>
                <span className={`type-badge type-${card.type}`}>{typeLabel}</span>
            </div>

            {/* Description */}
            {card.description && (
                <p style={{
                    fontSize: '12px',
                    color: '#8b949e',
                    lineHeight: '1.65',
                    margin: 0,
                    padding: '12px',
                    background: 'rgba(255,255,255,0.025)',
                    borderRadius: '8px',
                    border: '1px solid rgba(255,255,255,0.05)',
                }}>
                    {card.description}
                </p>
            )}

            {/* Parameters */}
            {isPlayable && paramKeys.length > 0 && (
                <div>
                    <p style={{
                        fontSize: '10px',
                        color: '#484f58',
                        textTransform: 'uppercase',
                        letterSpacing: '0.9px',
                        fontWeight: '700',
                        margin: '0 0 10px 2px',
                    }}>
                        {barricade ? 'Right-click 2 adjacent squares per edge' : 'Right-click squares to set'}
                    </p>
                    {barricade ? (
                        <BarricadeParams card={card} selectedParam={selectedParam} setSelectedParam={setSelectedParam} barricadeEdges={barricadeEdges} setBarricadeEdges={setBarricadeEdges} />
                    ) : (
                        paramKeys.map((key, i) => {
                            const value = card.param[key];
                            const isSet = value !== null;
                            const isActive = selectedParam === key;
                            return (
                                <div
                                    key={i}
                                    className={`param-row${isActive ? ' active' : ''}`}
                                    onClick={() => {
                                        if (isSet) {
                                            card.param[key] = null;
                                            setSelectedParam(key);
                                        }
                                    }}
                                    style={isSet ? {cursor: 'pointer'} : {cursor: 'default'}}
                                >
                                    <div style={{
                                        width: '8px', height: '8px', borderRadius: '50%', flexShrink: 0,
                                        background: isActive ? '#d4a843' : isSet ? '#3fb950' : '#484f58',
                                        boxShadow: isActive ? '0 0 8px rgba(212,168,67,0.5)' : 'none',
                                    }}/>
                                    <div style={{flex: 1, minWidth: 0}}>
                                        <div style={{fontSize: '12px', fontWeight: '600', color: '#c9d1d9', marginBottom: '3px'}}>
                                            {key}
                                        </div>
                                        <div style={{
                                            fontSize: '11px',
                                            color: isSet ? '#d4a843' : isActive ? '#8b949e' : '#484f58',
                                            fontFamily: 'monospace',
                                            letterSpacing: '0.3px',
                                        }}>
                                            {isSet ? value : isActive ? 'awaiting...' : '—'}
                                        </div>
                                    </div>
                                </div>
                            );
                        })
                    )}
                </div>
            )}

            {/* Play button */}
            {isPlayable && (
                <button
                    className="sotc-btn sotc-btn-gold"
                    style={{width: '100%', padding: '13px', fontSize: '14px'}}
                    onClick={playCardCallback}
                    disabled={!allParamsSet}
                >
                    {allParamsSet ? '▶ Play Card' : 'Set all parameters first'}
                </button>
            )}
            {!isPlayable && (
                <p style={{fontSize: '11px', color: '#484f58', textAlign: 'center', margin: 0, fontStyle: 'italic'}}>
                    This card cannot be played right now
                </p>
            )}
        </div>
    );
}
