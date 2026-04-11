export function HomeScreen({onPlaySolo, onMatchmaking}) {
    return (
        <div style={{
            minHeight: '100vh',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            background: 'radial-gradient(ellipse at 50% 30%, #1f1810 0%, #141008 40%, #0d0a05 100%)',
            gap: '40px',
            padding: '24px',
            position: 'relative',
            overflow: 'hidden',
        }}>
            {/* Ambient glow behind logo */}
            <div style={{
                position: 'absolute',
                top: '15%',
                left: '50%',
                transform: 'translateX(-50%)',
                width: '500px',
                height: '500px',
                background: 'radial-gradient(circle, rgba(201,150,58,0.12) 0%, rgba(212,168,67,0.05) 40%, transparent 70%)',
                pointerEvents: 'none',
            }}/>

            {/* Logo */}
            <img
                src={require('../assets/logo.svg')}
                alt="Storm on the Chess Board"
                style={{
                    width: '280px',
                    height: '280px',
                    filter: 'drop-shadow(0 0 40px rgba(201,150,58,0.3))',
                }}
            />

            {/* Title */}
            <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '16px'}}>
                <h1 style={{
                    fontSize: '36px',
                    fontWeight: '700',
                    letterSpacing: '1.5px',
                    textTransform: 'uppercase',
                    background: 'linear-gradient(135deg, #c9963a, #f0c860)',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    margin: 0,
                    textAlign: 'center',
                    lineHeight: 1.2,
                }}>
                    Storm on the Chess Board
                </h1>
                <p style={{
                    fontSize: '18px',
                    fontWeight: '500',
                    color: '#d4a843',
                    margin: 0,
                    textAlign: 'center',
                    fontStyle: 'italic',
                    opacity: 0.85,
                    letterSpacing: '0.3px',
                }}>
                    Vous êtes nul aux échecs, vengez-vous !
                </p>
            </div>

            {/* Buttons */}
            <div style={{display: 'flex', flexDirection: 'column', gap: '16px', width: '300px', maxWidth: '90vw'}}>
                <button
                    className="sotc-btn sotc-btn-gold"
                    style={{width: '100%', padding: '18px', fontSize: '17px', letterSpacing: '0.5px'}}
                    onClick={onPlaySolo}
                >
                    Jouer tout seul
                </button>
                <button
                    className="sotc-btn"
                    style={{
                        width: '100%',
                        padding: '18px',
                        fontSize: '17px',
                        letterSpacing: '0.5px',
                        borderColor: 'rgba(201,150,58,0.3)',
                        color: '#d4a843',
                    }}
                    onClick={onMatchmaking}
                >
                    Attendre un adversaire
                </button>
            </div>
        </div>
    );
}
