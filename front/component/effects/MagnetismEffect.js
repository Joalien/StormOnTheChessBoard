let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes magnetPulse {
            0%, 100% { transform: scale(1); opacity: 0.85; }
            50% { transform: scale(1.06); opacity: 1; }
        }
    `;
    document.head.appendChild(style);
}

export const MagnetismEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/MagnetismEffect.svg')})`,
            backgroundSize: 'cover',
            animation: 'magnetPulse 2s ease-in-out infinite'
        };
    }
};
