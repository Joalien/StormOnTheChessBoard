module.exports = {
    testEnvironment: 'jsdom',
    transform: {
        '^.+\\.[jt]sx?$': 'babel-jest',
    },
    transformIgnorePatterns: [
        'node_modules/(?!(react-native|react-native-web|expo|@expo|react-chessboard|chess\\.js)/)',
    ],
    moduleNameMapper: {
        '\\.(png|jpg|jpeg|gif|svg)$': '<rootDir>/__mocks__/fileMock.js',
        '^react-native$': 'react-native-web',
        '^\\./cardImages$': '<rootDir>/__mocks__/cardImagesMock.js',
    },
    setupFilesAfterEnv: ['@testing-library/jest-dom'],
};
