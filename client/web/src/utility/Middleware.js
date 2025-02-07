import { createProxyMiddleware } from 'http-proxy-middleware';

const host = '192.168.6.101:8000';

export default function(app) {
    app.use(
        '/api',
        createProxyMiddleware({
            target: [ `http://${host}`, `ws://${host}` ],
            changeOrigin: true,
        })
    );
}
