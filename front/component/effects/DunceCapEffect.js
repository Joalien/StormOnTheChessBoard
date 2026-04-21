let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes dunceWobble {
            0%,100%{transform:rotate(-2deg)}50%{transform:rotate(2deg)}
        }
    `;
    document.head.appendChild(style);
}

export const DunceCapEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/DunceCapEffect.svg')})`,
            backgroundSize: 'cover',
            animation: 'dunceWobble 2s ease-in-out infinite'
        };
    }
};
