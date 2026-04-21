let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes originsSpin {
            0%{transform:rotate(0deg)}100%{transform:rotate(360deg)}
        }
    `;
    document.head.appendChild(style);
}

export const OriginsEffect = {
    applyStyle: () => {
        injectStyles();
        return {
            backgroundImage: `url(${require('../../assets/images/effects/OriginsEffect.svg')})`,
            backgroundSize: 'cover',
            animation: 'originsSpin 8s ease-in-out infinite'
        };
    }
};
