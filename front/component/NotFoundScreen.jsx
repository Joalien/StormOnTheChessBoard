export function NotFoundScreen({onGoHome}) {
    return (
        <div style={{
            minHeight: '100vh',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            background: 'linear-gradient(160deg, #0a0d16 0%, #111520 50%, #0d1117 100%)',
            gap: '32px',
            textAlign: 'center',
            padding: '20px',
        }}>
            <div style={{fontSize: '120px', lineHeight: 1, userSelect: 'none'}}>
                ♞
            </div>
            <div style={{position: 'relative'}}>
                <h1 style={{
                    fontSize: '96px',
                    fontWeight: '800',
                    margin: 0,
                    background: 'linear-gradient(135deg, #c9963a, #f0c860)',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    letterSpacing: '4px',
                }}>
                    404
                </h1>
            </div>
            <p style={{
                fontSize: '20px',
                color: '#8b949e',
                margin: 0,
                maxWidth: '440px',
                lineHeight: 1.6,
            }}>
                Ce cavalier a saut&eacute; hors de l'&eacute;chiquier...
                <br />
                La page que vous cherchez n'existe pas.
            </p>
            <button
                className="sotc-btn sotc-btn-gold"
                style={{padding: '14px 32px', fontSize: '16px', marginTop: '8px'}}
                onClick={onGoHome}
            >
                Retour a l'accueil
            </button>
        </div>
    );
}
