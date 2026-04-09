import {Image} from "react-native";

const requireCard = require.context('../assets/images/cards', false, /\.png$/);

const TYPE_LABELS = {
    BEFORE_TURN:  'Before Move',
    AFTER_TURN:   'After Move',
    REPLACE_TURN: 'Replaces Move',
    ENEMY_TURN:   "Enemy's Turn",
};

export function CardParameters({card, selectedParam, setSelectedParam, playCardCallback}) {
    const fileName = `./${card.englishName}.png`;
    const hasImage = requireCard.keys().includes(fileName);
    const paramKeys = Object.keys(card.param || {});
    const allParamsSet = !Object.values(card.param || {}).some(x => x === null);
    const typeLabel = TYPE_LABELS[card.type] || card.type;

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
                        <Image source={requireCard(fileName)} style={{width: 126, height: 180}}/>
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
            {paramKeys.length > 0 && (
                <div>
                    <p style={{
                        fontSize: '10px',
                        color: '#484f58',
                        textTransform: 'uppercase',
                        letterSpacing: '0.9px',
                        fontWeight: '700',
                        margin: '0 0 10px 2px',
                    }}>
                        Parameters
                    </p>
                    {paramKeys.map((key, i) => {
                        const value = card.param[key];
                        const isSet = value !== null;
                        const isActive = selectedParam === key;
                        return (
                            <label
                                key={i}
                                className={`param-row${isActive ? ' active' : ''}`}
                                onClick={() => setSelectedParam(key)}
                            >
                                <input
                                    type="radio"
                                    className="param-radio"
                                    name="param"
                                    checked={isActive}
                                    onChange={() => setSelectedParam(key)}
                                    onClick={e => e.stopPropagation()}
                                />
                                <div style={{flex: 1, minWidth: 0}}>
                                    <div style={{fontSize: '12px', fontWeight: '600', color: '#c9d1d9', marginBottom: '3px'}}>
                                        {key}
                                    </div>
                                    <div style={{
                                        fontSize: '11px',
                                        color: isSet ? '#d4a843' : '#f85149',
                                        fontFamily: 'monospace',
                                        letterSpacing: '0.3px',
                                    }}>
                                        {isSet ? value : 'Right-click a square to set'}
                                    </div>
                                </div>
                            </label>
                        );
                    })}
                </div>
            )}

            {/* Play button */}
            <button
                className="sotc-btn sotc-btn-gold"
                style={{width: '100%', padding: '13px', fontSize: '14px'}}
                onClick={playCardCallback}
                disabled={!allParamsSet}
            >
                {allParamsSet ? '▶ Play Card' : 'Set all parameters first'}
            </button>
        </div>
    );
}
