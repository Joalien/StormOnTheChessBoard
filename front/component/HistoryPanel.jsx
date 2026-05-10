import {useEffect, useRef} from 'react';

const COLOR_STYLES = {
    white: {dot: '#e6edf3', label: '#c9d1d9'},
    black: {dot: '#1a1f2e', label: '#a78bfa'},
    system: {dot: '#484f58', label: '#484f58'},
};

export function HistoryPanel({history}) {
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
                    return (
                        <div key={entry.index} style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '8px',
                            fontSize: '11px',
                            padding: '3px 6px',
                            borderRadius: '4px',
                            background: 'rgba(255,255,255,0.02)',
                        }}>
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
