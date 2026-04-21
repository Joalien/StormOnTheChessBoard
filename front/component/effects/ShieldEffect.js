let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes shieldGlow {
            0%,100%{opacity:0.5;filter:brightness(1)}50%{opacity:0.8;filter:brightness(1.3)}
        }
    `;
    document.head.appendChild(style);
}

export const ShieldEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/ShieldEffect.svg')})`,
            backgroundSize: 'cover',
            animation: 'shieldGlow 3s ease-in-out infinite'
        };
    }
};
