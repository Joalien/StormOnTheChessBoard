export function HomeScreen({onPlaySolo, onMatchmaking}) {
    return (
        <div style={{
            minHeight: '100vh',
            width: '100%',
            display: 'flex',
            flexDirection: 'row',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '48px',
            position: 'relative',
            overflow: 'hidden',
        }}>
            {/* Blurred background fill */}
            <div style={{
                position: 'absolute',
                inset: 0,
                backgroundImage: `url(${require('../assets/background.png')})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                filter: 'blur(20px)',
                transform: 'scale(1.1)',
                zIndex: 0,
            }}/>
            {/* Sharp centered image */}
            <div style={{
                position: 'absolute',
                inset: 0,
                backgroundImage: `url(${require('../assets/background.png')})`,
                backgroundSize: 'auto 100vh',
                backgroundPosition: 'center center',
                backgroundRepeat: 'no-repeat',
                zIndex: 1,
            }}/>
            <div style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: '28px',
                padding: '32px 40px',
                borderRadius: '16px',
                background: 'rgba(13,10,5,0.7)',
                backdropFilter: 'blur(3px)',
                zIndex: 2,
            }}>
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
                    Tempête sur l'Échiquier
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
                    Vous êtes nul aux échecs ?<br/>
                    <strong>Vengez-vous !</strong>
                </p>

                {/* Buttons */}
                <div style={{display: 'flex', flexDirection: 'column', gap: '16px', width: '300px'}}>
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
        </div>
    );
}
