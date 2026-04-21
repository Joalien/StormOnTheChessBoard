let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes astralFloat {
            0%,100%{opacity:0.4;transform:scale(1) translateY(0)}50%{opacity:0.7;transform:scale(1.05) translateY(-3px)}
        }
    `;
    document.head.appendChild(style);
}

export const AstralTravelEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/AstralTravelEffect.svg')})`,
            backgroundSize: 'cover',
            animation: 'astralFloat 3s ease-in-out infinite'
        };
    }
};
