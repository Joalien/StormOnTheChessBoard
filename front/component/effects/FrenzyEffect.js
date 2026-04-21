let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes frenzyBurn {
            0%,100%{opacity:0.5;transform:scale(1)}50%{opacity:0.9;transform:scale(1.1)}
        }
    `;
    document.head.appendChild(style);
}

export const FrenzyEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/FrenzyEffect.svg')})`,
            backgroundSize: 'cover',
            animation: 'frenzyBurn 1.5s ease-in-out infinite'
        };
    }
};
