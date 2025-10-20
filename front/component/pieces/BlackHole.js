let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes rotate {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }
    `;
    document.head.appendChild(style);
}

export const BlackHole = ({ squareWidth }) => {
    injectStyles();
    return (
        <img
            src={require('../../assets/images/effects/BlackHoleEffect.png')}
            alt="BlackHole"
            style={{
                width: squareWidth,
                height: squareWidth,
                animation: 'rotate 60s linear infinite'
            }}
        />
    );
}