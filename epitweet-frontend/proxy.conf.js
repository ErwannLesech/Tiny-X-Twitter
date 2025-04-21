const PROXY_CONFIG = {
    "/api/*": {
      "target": "http://localhost:8081",
      "secure": false,
      "logLevel": "debug",
      "changeOrigin": true,
      "pathRewrite": {"^/api": ""},
      "router": function(req) {
        if (req.url.includes('/api/social')) return 'http://localhost:8084';
        if (req.url.includes('/api/search')) return 'http://localhost:8083';
        if (req.url.includes('/api/posts')) return 'http://localhost:8082';
        if (req.url.includes('/api/timeline/home')) return 'http://localhost:8086';
        return 'http://localhost:8081'; // default
      }
    }
};
  
module.exports = PROXY_CONFIG;