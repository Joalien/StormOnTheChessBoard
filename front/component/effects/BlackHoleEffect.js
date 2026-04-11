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

export const BlackHoleEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/BlackHoleEffect.png')})`,
            backgroundSize: 'cover',
            animation: 'rotate 60s linear infinite'
        };
    }
};
