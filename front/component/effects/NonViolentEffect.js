let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes haloPulse {
            0%,100%{opacity:0.85}50%{opacity:1}
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
            animation: 'haloPulse 2.5s ease-in-out infinite'
        };
    }
};
