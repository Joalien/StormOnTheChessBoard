import {useEffect, useMemo, useState} from 'react';

const CONFETTI_COUNT = 80;
const CONFETTI_COLORS = ['#d4a843', '#c9963a', '#f0c860', '#3fb950', '#a78bfa', '#f85149', '#38bdf8', '#e8b84b'];

function generateConfetti(count) {
    return Array.from({length: count}, (_, i) => ({
        id: i,
        left: Math.random() * 100,
        delay: Math.random() * 2.5,
        duration: 2.8 + Math.random() * 2.2,
        size: 6 + Math.random() * 8,
        color: CONFETTI_COLORS[i % CONFETTI_COLORS.length],
        rotation: Math.random() * 360,
        xDrift: -30 + Math.random() * 60,
    }));
}

function injectStyles() {
    if (typeof document === 'undefined') return;
    if (document.getElementById('sotc-victory-popup-styles')) return;
    const style = document.createElement('style');
    style.id = 'sotc-victory-popup-styles';
    style.textContent = `
        @keyframes sotc-confetti-fall {
            0% { transform: translate(0, -20vh) rotate(0deg); opacity: 1; }
            100% { transform: translate(var(--x-drift), 120vh) rotate(720deg); opacity: 0.85; }
        }
        @keyframes sotc-victory-pop {
            0% { transform: scale(0.6); opacity: 0; }
            70% { transform: scale(1.04); opacity: 1; }
            100% { transform: scale(1); opacity: 1; }
        }
        @keyframes sotc-victory-fade {
            from { opacity: 0; }
            to { opacity: 1; }
        }
    `;
    document.head.appendChild(style);
}

const TITLES = {
    win: <>Vous avez <em style={{fontStyle: 'normal', color: '#f0c860'}}>gagné !</em></>,
    lose: 'Vous avez perdu',
    draw: 'Match nul',
};

export function VictoryPopup({outcome, onClose, onGoHome}) {
    const [visible, setVisible] = useState(true);
    const confetti = useMemo(() => (outcome === 'win' ? generateConfetti(CONFETTI_COUNT) : []), [outcome]);

    useEffect(() => {
        injectStyles();
    }, []);

    if (!visible) return null;

    const handleClose = () => {
        setVisible(false);
        if (onClose) onClose();
    };

    return (
        <div
            style={{
                position: 'fixed',
                inset: 0,
                zIndex: 10000,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                background: 'rgba(0,0,0,0.65)',
                backdropFilter: 'blur(4px)',
                animation: 'sotc-victory-fade 0.3s ease',
            }}
            onClick={handleClose}
        >
            {outcome === 'win' && (
                <div style={{position: 'absolute', inset: 0, overflow: 'hidden', pointerEvents: 'none'}}>
                    {confetti.map(c => (
                        <div
                            key={c.id}
                            style={{
                                position: 'absolute',
                                top: 0,
                                left: `${c.left}%`,
                                width: `${c.size}px`,
                                height: `${c.size * 1.6}px`,
                                background: c.color,
                                borderRadius: '2px',
                                '--x-drift': `${c.xDrift}vw`,
                                animation: `sotc-confetti-fall ${c.duration}s linear ${c.delay}s infinite`,
                                transform: `rotate(${c.rotation}deg)`,
                                opacity: 0.9,
                                boxShadow: `0 0 4px ${c.color}`,
                            }}
                        />
                    ))}
                </div>
            )}

            <div
                onClick={e => e.stopPropagation()}
                style={{
                    position: 'relative',
                    background: 'linear-gradient(145deg, rgba(22,27,34,0.98), rgba(13,17,23,0.98))',
                    border: '1px solid rgba(255,255,255,0.12)',
                    borderRadius: '20px',
                    padding: '48px 56px',
                    minWidth: '380px',
                    maxWidth: '90vw',
                    textAlign: 'center',
                    boxShadow: '0 30px 80px rgba(0,0,0,0.8), 0 0 120px rgba(212,168,67,0.15)',
                    animation: 'sotc-victory-pop 0.5s cubic-bezier(0.34, 1.56, 0.64, 1)',
                }}
            >
                <h1 style={{
                    fontSize: '42px',
                    fontWeight: 800,
                    margin: '0 0 16px',
                    background: outcome === 'win'
                        ? 'linear-gradient(135deg, #d4a843, #f0c860)'
                        : outcome === 'lose'
                            ? 'linear-gradient(135deg, #8b949e, #c9d1d9)'
                            : 'linear-gradient(135deg, #a78bfa, #c9d1d9)',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    letterSpacing: '0.5px',
                }}>
                    {TITLES[outcome]}
                </h1>
                <p style={{fontSize: '14px', color: '#8b949e', margin: '0 0 32px', lineHeight: 1.6}}>
                    {outcome === 'win' && 'Votre adversaire n’a plus de coup légal.'}
                    {outcome === 'lose' && 'Vous n’avez plus de coup légal.'}
                    {outcome === 'draw' && 'Aucun coup légal n’est possible sans être en échec.'}
                </p>
                <div style={{display: 'flex', gap: '12px', justifyContent: 'center'}}>
                    <button className="sotc-btn" onClick={handleClose} style={{flex: 1, padding: '12px 20px', fontSize: '14px'}}>
                        Voir la partie
                    </button>
                    <button className="sotc-btn sotc-btn-gold" onClick={onGoHome} style={{flex: 1, padding: '12px 20px', fontSize: '14px'}}>
                        Retour à l&apos;accueil
                    </button>
                </div>
            </div>
        </div>
    );
}
