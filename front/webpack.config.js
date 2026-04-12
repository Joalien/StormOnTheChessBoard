const createExpoWebpackConfigAsync = require('@expo/webpack-config');

module.exports = async function (env, argv) {
    const config = await createExpoWebpackConfigAsync(env, argv);

    // Exclude test dependencies from bundling to speed up Expo dev server
    config.externals = [
        ...(config.externals || []),
        /^jest/,
        /^@testing-library/,
        /^babel-jest$/,
        /^identity-obj-proxy$/,
        /^jest-environment-jsdom$/,
    ];

    return config;
};
