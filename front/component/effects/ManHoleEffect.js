let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes pulseBackground {
            0%, 100% {
                background-size: 100% 100%;
                background-position: center;
            }
            50% {
                background-size: 105% 105%;
                background-position: center;
            }
        }
    `;
    document.head.appendChild(style);
}

export const ManHoleEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/ManHoleEffect.png')})`,
            backgroundSize: '100% 100%',
            backgroundPosition: 'center',
            animation: 'pulseBackground 4s ease-in-out infinite'
        };
    }
};