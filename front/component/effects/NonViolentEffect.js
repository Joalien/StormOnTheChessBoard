let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes peacePulse {
            0%,100%{opacity:0.5;transform:scale(1)}50%{opacity:0.7;transform:scale(1.05)}
        }
    `;
    document.head.appendChild(style);
}

export const NonViolentEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/NonViolentEffect.svg')})`,
            backgroundSize: 'cover',
            animation: 'peacePulse 3.5s ease-in-out infinite'
        };
    }
};
