import React, {useState, useRef, useEffect} from 'react';
import cardImages from '../cardImages';

const ALL_CARD_NAMES = Object.keys(cardImages);

const backendOrigin = typeof window !== 'undefined' ? window.location.origin : 'http://localhost:9000';

export function DevCardSearch({gameId, onReplaced}) {
    const [query, setQuery] = useState('');
    const [open, setOpen] = useState(false);
    const [status, setStatus] = useState(null); // 'ok' | 'error'
    const inputRef = useRef(null);

    const filtered = query.length === 0
        ? ALL_CARD_NAMES
        : ALL_CARD_NAMES.filter(name => name.toLowerCase().includes(query.toLowerCase()));

    useEffect(() => {
        if (open) inputRef.current?.focus();
    }, [open]);

    async function replaceCard(cardName) {
        setStatus(null);
        try {
            const res = await fetch(`${backendOrigin}/api/dev/${gameId}/hand/replace-last`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({cardName}),
            });
            if (!res.ok) throw new Error(await res.text());
            setStatus('ok');
            setQuery('');
            setOpen(false);
            onReplaced?.();
        } catch {
            setStatus('error');
        }
    }

    return (
        <div style={{
            border: '1px dashed #ff4444',
            borderRadius: '8px',
            padding: '10px 12px',
            background: 'rgba(60, 0, 0, 0.35)',
        }}>
            <button
                onClick={() => setOpen(o => !o)}
                style={{
                    width: '100%',
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                    padding: 0,
                }}
            >
                <span style={{fontSize: '10px', color: '#ff4444', fontWeight: '700', letterSpacing: '0.8px', textTransform: 'uppercase'}}>
                    DEV
                </span>
                <span style={{fontSize: '12px', color: '#cc8888', flex: 1, textAlign: 'left'}}>
                    Remplacer la dernière carte
                </span>
                <span style={{color: '#cc8888', fontSize: '11px'}}>{open ? '▲' : '▼'}</span>
            </button>

            {open && (
                <div style={{marginTop: '8px', display: 'flex', flexDirection: 'column', gap: '6px'}}>
                    <input
                        ref={inputRef}
                        value={query}
                        onChange={e => setQuery(e.target.value)}
                        placeholder="Rechercher une carte…"
                        style={{
                            width: '100%',
                            padding: '6px 8px',
                            background: '#1a1a2e',
                            border: '1px solid #444',
                            borderRadius: '6px',
                            color: '#fff',
                            fontSize: '12px',
                            boxSizing: 'border-box',
                        }}
                    />
                    {status === 'ok' && <span style={{color: '#44ff88', fontSize: '11px'}}>Carte remplacée</span>}
                    {status === 'error' && <span style={{color: '#ff4444', fontSize: '11px'}}>Erreur</span>}
                    <div style={{
                        maxHeight: '160px',
                        overflowY: 'auto',
                        display: 'flex',
                        flexDirection: 'column',
                        gap: '2px',
                    }}>
                        {filtered.map(name => (
                            <button
                                key={name}
                                onClick={() => replaceCard(name)}
                                style={{
                                    background: 'none',
                                    border: 'none',
                                    cursor: 'pointer',
                                    textAlign: 'left',
                                    padding: '4px 6px',
                                    borderRadius: '4px',
                                    color: '#cc8888',
                                    fontSize: '12px',
                                }}
                                onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,100,100,0.15)'}
                                onMouseLeave={e => e.currentTarget.style.background = 'none'}
                            >
                                {name.replace(/Card$/, '')}
                            </button>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}
