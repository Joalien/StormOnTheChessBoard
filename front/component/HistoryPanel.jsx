import {useEffect, useRef} from 'react';

const COLOR_STYLES = {
    white: {dot: '#e6edf3', label: '#c9d1d9'},
    black: {dot: '#1a1f2e', label: '#a78bfa'},
    system: {dot: '#484f58', label: '#484f58'},
};

export function HistoryPanel({history, onCardEntryClick, selectedHistoryIndex}) {
    const scrollRef = useRef(null);
    const length = (history || []).length;

    useEffect(() => {
        if (scrollRef.current) {
            scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
        }
    }, [length]);

    if (length === 0) return null;

    return (
        <div className="sotc-panel" style={{padding: '12px 16px', display: 'flex', flexDirection: 'column', gap: '8px'}}>
            <span style={{fontSize: '11px', fontWeight: '700', color: '#484f58', textTransform: 'uppercase', letterSpacing: '0.8px'}}>
                Historique
            </span>
            <div ref={scrollRef} style={{
                display: 'flex',
                flexDirection: 'column',
                gap: '4px',
                maxHeight: '240px',
                overflowY: 'auto',
                paddingRight: '4px',
            }}>
                {history.map(entry => {
                    const style = COLOR_STYLES[entry.color] || COLOR_STYLES.system;
                    const isCard = !!entry.cardEnglishName;
                    const isSelected = selectedHistoryIndex === entry.index;
                    return (
                        <div
                            key={entry.index}
                            onClick={isCard && onCardEntryClick ? () => onCardEntryClick(entry) : undefined}
                            style={{
                                display: 'flex',
                                alignItems: 'center',
                                gap: '8px',
                                fontSize: '11px',
                                padding: '3px 6px',
                                borderRadius: '4px',
                                background: isSelected
                                    ? 'rgba(212,168,67,0.18)'
                                    : isCard
                                        ? 'rgba(167,139,250,0.06)'
                                        : 'rgba(255,255,255,0.02)',
                                cursor: isCard ? 'pointer' : 'default',
                                border: isSelected ? '1px solid rgba(212,168,67,0.6)' : '1px solid transparent',
                                transition: 'background 0.12s, border-color 0.12s',
                            }}
                            onMouseEnter={isCard ? e => {
                                if (!isSelected) e.currentTarget.style.background = 'rgba(167,139,250,0.14)';
                            } : undefined}
                            onMouseLeave={isCard ? e => {
                                if (!isSelected) e.currentTarget.style.background = 'rgba(167,139,250,0.06)';
                            } : undefined}
                        >
                            <span style={{
                                width: '10px',
                                height: '10px',
                                borderRadius: '50%',
                                background: style.dot,
                                border: '1px solid rgba(255,255,255,0.15)',
                                flexShrink: 0,
                            }}/>
                            <span style={{
                                color: '#484f58',
                                fontVariantNumeric: 'tabular-nums',
                                minWidth: '20px',
                            }}>
                                {entry.index}
                            </span>
                            <span style={{
                                color: style.label,
                                lineHeight: '1.4',
                                wordBreak: 'break-word',
                            }}>
                                {entry.action}
                            </span>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
