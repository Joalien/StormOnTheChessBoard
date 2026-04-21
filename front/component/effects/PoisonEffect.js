let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes poisonPulse {
            0%,100%{opacity:0.6;transform:scale(1)}50%{opacity:0.9;transform:scale(1.08)}
        }
    `;
    document.head.appendChild(style);
}

export const PoisonEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/PoisonEffect.svg')})`,
            backgroundSize: 'cover',
            animation: 'poisonPulse 2.5s ease-in-out infinite'
        };
    }
};
