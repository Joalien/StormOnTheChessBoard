let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes madSpin {
            0%{transform:rotate(0deg) scale(1)}50%{transform:rotate(3deg) scale(1.04)}100%{transform:rotate(0deg) scale(1)}
        }
    `;
    document.head.appendChild(style);
}

export const MaddeningSquareEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/MaddeningSquareEffect.svg')})`,
            backgroundSize: 'cover',
            animation: 'madSpin 2s ease-in-out infinite'
        };
    }
};
