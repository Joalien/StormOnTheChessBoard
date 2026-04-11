export function WaitingScreen({onCancel}) {
    return (
        <div style={{
            minHeight: '100vh',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            background: 'linear-gradient(160deg, #0a0d16 0%, #111520 50%, #0d1117 100%)',
            gap: '48px',
        }}>
            <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '16px'}}>
                <span style={{fontSize: '64px', lineHeight: 1}}>♞</span>
                <h1 style={{
                    fontSize: '32px',
                    fontWeight: '700',
                    letterSpacing: '1.2px',
                    textTransform: 'uppercase',
                    background: 'linear-gradient(135deg, #c9963a, #f0c860)',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    margin: 0,
                }}>
                    Storm on the Chess Board
                </h1>
            </div>

            <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '24px'}}>
                <div style={{display: 'flex', alignItems: 'center', gap: '12px'}}>
                    <div style={{
                        width: '20px',
                        height: '20px',
                        border: '3px solid #c9963a',
                        borderTopColor: 'transparent',
                        borderRadius: '50%',
                        animation: 'spin 1s linear infinite',
                    }}/>
                    <span style={{
                        fontSize: '18px',
                        color: '#c9d1d9',
                        fontWeight: '500',
                    }}>
                        Recherche d'un adversaire...
                    </span>
                </div>

                <button
                    className="sotc-btn"
                    style={{padding: '12px 32px', fontSize: '14px'}}
                    onClick={onCancel}
                >
                    Annuler
                </button>
            </div>

            <style>{`
                @keyframes spin {
                    to { transform: rotate(360deg); }
                }
            `}</style>
        </div>
    );
}
