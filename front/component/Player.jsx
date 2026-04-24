import {Card} from "./Card";

function formatTime(seconds) {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${String(s).padStart(2, '0')}`;
}

export function Player({player, hiddenCards, showCard, color, selectedCard, playableTypes, time, isActivePlayer, large = false}) {
    const isBlack = color === 'black';
    const cards = player.cards || [];

    return (
        <div style={{width: '100%', display: 'flex', flexDirection: 'column', gap: '8px'}}>
            {/* Player label */}
            <div style={{display: 'flex', alignItems: 'center', gap: '7px', paddingLeft: '2px'}}>
                <div style={{
                    width: '18px',
                    height: '18px',
                    borderRadius: '50%',
                    background: isBlack ? '#1a1f2e' : '#e6edf3',
                    border: `2px solid ${isBlack ? 'rgba(167,139,250,0.45)' : 'rgba(230,237,243,0.4)'}`,
                    flexShrink: 0,
                }}/>
                <span style={{
                    fontSize: '11px',
                    fontWeight: '700',
                    color: isBlack ? '#a78bfa' : '#c9d1d9',
                    textTransform: 'uppercase',
                    letterSpacing: '0.9px',
                }}>
                    {isBlack ? 'Noirs' : 'Blancs'}
                </span>
                {!hiddenCards && (
                    <span style={{
                        fontSize: '10px',
                        color: '#484f58',
                        background: 'rgba(255,255,255,0.04)',
                        padding: '2px 7px',
                        borderRadius: '8px',
                        border: '1px solid rgba(255,255,255,0.06)',
                    }}>
                        {cards.length} carte{cards.length !== 1 ? 's' : ''}
                    </span>
                )}
                {time !== undefined && (
                    <span style={{
                        fontSize: '11px',
                        fontVariantNumeric: 'tabular-nums',
                        color: isActivePlayer ? '#e6edf3' : '#484f58',
                        background: isActivePlayer ? 'rgba(255,255,255,0.07)' : 'rgba(255,255,255,0.02)',
                        padding: '2px 8px',
                        borderRadius: '8px',
                        border: `1px solid ${isActivePlayer ? 'rgba(255,255,255,0.15)' : 'rgba(255,255,255,0.05)'}`,
                        fontFamily: 'monospace',
                        marginLeft: 'auto',
                    }}>
                        {formatTime(time)}
                    </span>
                )}
            </div>

            {/* Cards row */}
            <div style={{display: 'flex', gap: large ? '12px' : '8px', flexWrap: 'wrap', minHeight: large ? '190px' : '116px', alignItems: 'flex-end'}}>
                {cards.map((card, index) => {
                    const isPlayable = !playableTypes || playableTypes.includes(card.type);
                    return (
                        <Card
                            key={index}
                            hidden={hiddenCards}
                            name={card.englishName}
                            showCard={() => showCard(card)}
                            isSelected={!hiddenCards && selectedCard && selectedCard.englishName === card.englishName}
                            isPlayable={isPlayable}
                            large={large}
                        />
                    );
                })}
                {cards.length === 0 && (
                    <span style={{fontSize: '12px', color: '#484f58', alignSelf: 'center', fontStyle: 'italic'}}>Aucune carte</span>
                )}
            </div>
        </div>
    );
}
