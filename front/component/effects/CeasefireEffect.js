let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes ceasePulse {
            0%,100%{opacity:0.5;transform:scale(1)}50%{opacity:0.8;transform:scale(1.06)}
        }
    `;
    document.head.appendChild(style);
}

export const CeasefireEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/CeasefireEffect.svg')})`,
            backgroundSize: 'cover',
            animation: 'ceasePulse 2.5s ease-in-out infinite'
        };
    }
};
