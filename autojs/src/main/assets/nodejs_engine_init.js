var __requireOld__ = require;
var __nodejs_modules__ = {
    'websocket' : com.iwebpp.wspp.WebSocket,
    'websocketserver': com.iwebpp.wspp.WebSocketServer,
    'net': com.iwebpp.node.net.TCP,
    'tcp': com.iwebpp.node.net.TCP,
    'udt': com.iwebpp.node.net.UDT,
    'readable': com.iwebpp.node.stream.Readable2,
    'writable': com.iwebpp.node.stream.Writable2,
    'duplex': com.iwebpp.node.stream.Duplex,
    'transform': com.iwebpp.node.stream.Transform,
    'passthrough': com.iwebpp.node.stream.PassThrough,
    'dns': com.iwebpp.node.Dns,
    'url': com.iwebpp.node.Url,
}

var require = function(module){
    if (module === 'http'){
        return {
            get: function(url, listener){
                return com.iwebpp.node.http.http.get(NodeCurrentContext, url, listener);
            },
            request: function(url, listener) {
                return com.iwebpp.node.http.http.request(NodeCurrentContext, url, listener);
            },
            createServer: function(listener) {
                return com.iwebpp.node.http.http.createServer(NodeCurrentContext, listener);
            },
        };
    }
    if (module === 'httpp'){
        return {
            get: function(url, listener){
                return com.iwebpp.node.http.httpp.get(NodeCurrentContext, url, listener);
            },
            request: function(url, listener) {
                return com.iwebpp.node.http.httpp.request(NodeCurrentContext, url, listener);
            },
            createServer: function(listener) {
                return com.iwebpp.node.http.httpp.createServer(NodeCurrentContext, listener);
            },
        };
    }

    return __requireOld__(module);
};
