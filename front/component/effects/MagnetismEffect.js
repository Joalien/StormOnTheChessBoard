let stylesInjected = false;

function injectStyles() {
    if (stylesInjected) return;
    stylesInjected = true;

    const style = document.createElement('style');
    style.textContent = `
        @keyframes rotate {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }
    `;
    document.head.appendChild(style);
}

export const MagnetismEffect = {
    applyStyle: () => {
        injectStyles(); // Injecte le CSS la première fois
        return {
            backgroundImage: `url(${require('./magnet_icon.gif')})`,
            backgroundSize: 'cover',
            animation: 'rotate 10s ease-in-out infinite'
        };
    }
};