const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    app.use(
        '/api',
        createProxyMiddleware({
            target: ['http://192.168.6.101:8000', 'ws://192.168.6.101:8000'],
            changeOrigin: true,
        })
    );
};
