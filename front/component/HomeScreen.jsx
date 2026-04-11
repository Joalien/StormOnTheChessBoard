import {toast} from "react-toastify";

export function HomeScreen({onPlaySolo}) {
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

            <div style={{display: 'flex', flexDirection: 'column', gap: '16px', width: '280px'}}>
                <button
                    className="sotc-btn sotc-btn-gold"
                    style={{width: '100%', padding: '16px', fontSize: '16px'}}
                    onClick={onPlaySolo}
                >
                    Jouer tout seul
                </button>
                <button
                    className="sotc-btn"
                    style={{width: '100%', padding: '16px', fontSize: '16px'}}
                    onClick={() => toast.info("Coming soon!")}
                >
                    Attendre un adversaire
                </button>
            </div>
        </div>
    );
}
