package com.stardust;

/**
 * Created by Stardust on 2017/5/8.
 */

// Copyright (c) 2014 Tom Zhou<iwebpp@gmail.com>


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.support.test.filters.SmallTest;
import android.util.Log;

import com.iwebpp.node.EventEmitter.Listener;
import com.iwebpp.node.NodeContext;
import com.iwebpp.node.NodeContext.TimeoutListener;
import com.iwebpp.node.http.ClientRequest;
import com.iwebpp.node.http.ClientRequest.upgradeListener;
import com.iwebpp.node.http.HttpServer;
import com.iwebpp.node.http.HttpServer.clientErrorListener;
import com.iwebpp.node.http.http;
import com.iwebpp.node.http.IncomingMessage;
import com.iwebpp.node.http.ReqOptions;
import com.iwebpp.node.http.ServerResponse;
import com.iwebpp.node.net.AbstractSocket;
import com.iwebpp.node.stream.Writable.WriteCB;

import org.junit.*;

import static org.junit.Assert.fail;

@SmallTest
public final class NodeTest {
    private static final String TAG = "HttpTest";
    private NodeContext ctx;

    @Test
    public void testListening() throws Exception {
        HttpServer srv;
        final int port = 6188;
        srv = new HttpServer(ctx);

        srv.listen(port, "127.0.0.1", 4000, new HttpServer.ListeningCallback() {

            @Override
            public void onListening() throws Exception {
                Log.d(TAG, "http server listening on " + port);
            }
        });
        Thread.sleep(8000);
    }

    @org.junit.Test
    public void testConnection() throws Exception {
        final int port = 6288;
        final HttpServer srv = new HttpServer(ctx, new HttpServer.requestListener() {

            @Override
            public void onRequest(IncomingMessage req, ServerResponse res)
                    throws Exception {
                Log.d(TAG, "got reqeust, headers: " + req.headers());

                Map<String, List<String>> headers = new Hashtable<String, List<String>>();
                headers.put("content-type", new ArrayList<String>());
                headers.get("content-type").add("text/plain");
                ///headers.put("te", new LinkedList<String>());
                ///headers.get("te").add("chunk");

                res.writeHead(200, headers);
                ///for (int i = 0; i < 10; i ++)
                res.write("Hello Tom", "utf-8", new WriteCB() {

                    @Override
                    public void writeDone(String error) throws Exception {
                        Log.d(TAG, "http res.write done");
                        fail("http res.write done");
                    }

                });

                res.end(null, null, null);
                ;

            }

        });

        srv.onClientError(new clientErrorListener() {

            @Override
            public void onClientError(String exception, AbstractSocket socket) throws Exception {
                Log.e(TAG, "client error: " + exception + "@" + socket);
                fail("client error: " + exception + "@" + socket);
            }

        });

        srv.listen(port, "0.0.0.0", 10, new HttpServer.ListeningCallback() {

            @Override
            public void onListening() throws Exception {
                Log.d(TAG, "http server listening on " + port);
            }
        });
    }

    @org.junit.Test
    public void testUpgrade() throws Exception {
        final String host = "192.188.1.100";
        final int port = 6668;

        // client
        ReqOptions ropt = new ReqOptions();
        ropt.hostname = host;
        ropt.port = port;
        ropt.method = "GET";
        ropt.path = "/";

        ropt.headers.put("Connection", new ArrayList<String>());
        ropt.headers.get("Connection").add("Upgrade");

        ropt.headers.put("Upgrade", new ArrayList<String>());
        ropt.headers.get("Upgrade").add("websocket");

        ropt.headers.put("Host", new ArrayList<String>());
        ropt.headers.get("Host").add("192.188.1.100:6668");

        ropt.headers.put("Origin", new ArrayList<String>());
        ropt.headers.get("Origin").add("http://192.188.1.100:6668");

        ropt.headers.put("Sec-WebSocket-Version", new ArrayList<String>());
        ropt.headers.get("Sec-WebSocket-Version").add("13");

        ropt.headers.put("Sec-WebSocket-Key", new ArrayList<String>());
        ropt.headers.get("Sec-WebSocket-Key").add("MTMtVHVlIE9jdCAwNyAxMzozNzoyMiBHTVQrMDg6MDAgMjAxNA==");

        ClientRequest req = http.request(ctx, ropt, new ClientRequest.responseListener() {

            @Override
            public void onResponse(IncomingMessage res) throws Exception {
                Log.d(TAG, "STATUS: " + res.statusCode());
                Log.d(TAG, "HEADERS: " + res.getHeaders());

                res.setEncoding("utf-8");
                res.on("data", new Listener() {

                    @Override
                    public void onEvent(Object chunk) throws Exception {
                        Log.d(TAG, "BODY: " + chunk);

                    }

                });
            }

        });

        req.onceUpgrade(new upgradeListener() {

            @Override
            public void onUpgrade(IncomingMessage res,
                                  AbstractSocket socket, ByteBuffer head)
                    throws Exception {
                Log.d(TAG, "got upgrade: " + res.toString());
            }

        });

        req.on("error", new Listener() {

            @Override
            public void onEvent(Object e) throws Exception {
                Log.d(TAG, "problem with request: " + e);
                fail("problem with request: " + e);
            }

        });

        req.end(null, null, null);

    }

    @org.junit.Test
    public void testConnect() throws Exception {
        final String host = "192.188.1.100";
        final int port = 51680;

        // client
        ReqOptions ropt = new ReqOptions();
        ropt.hostname = host;
        ropt.port = port;
        ropt.method = "PUT";
        ropt.path = "/";
        ///ropt.keepAlive = true;
        ///ropt.keepAliveMsecs = 10000;

        ClientRequest req = http.request(ctx, ropt, new ClientRequest.responseListener() {

            @Override
            public void onResponse(IncomingMessage res) throws Exception {
                Log.d(TAG, "STATUS: " + res.statusCode());
                Log.d(TAG, "HEADERS: " + res.getHeaders());

                res.setEncoding("utf-8");
                res.on("data", new Listener() {

                    @Override
                    public void onEvent(Object chunk) throws Exception {
                        Log.d(TAG, "BODY: " + chunk);

                    }

                });
            }

        });

        req.on("error", new Listener() {

            @Override
            public void onEvent(Object e) throws Exception {
                Log.d(TAG, "problem with request: " + e);
                fail("problem with request: " + e);
            }

        });

        // write data to request body
        for (int i = 0; i < 8; i++)
            req.write("data" + i + "\n", "utf-8", null);

        req.end(null, null, null);

    }

    @org.junit.Test
    public void testConnectPair() throws Exception {
        final int port = 6688;

        final HttpServer srv = http.createServer(ctx, new HttpServer.requestListener() {

            @Override
            public void onRequest(IncomingMessage req, ServerResponse res)
                    throws Exception {
                Log.d(TAG, "got reqeust, headers: " + req.headers());

                Map<String, List<String>> headers = new Hashtable<String, List<String>>();
                headers.put("content-type", new ArrayList<String>());
                headers.get("content-type").add("text/plain");
                ///headers.put("te", new ArrayList<String>());
                ///headers.get("te").add("chunk");

                res.writeHead(200, headers);
                res.write("Hello Tom", "utf-8", new WriteCB() {

                    @Override
                    public void writeDone(String error) throws Exception {
                        Log.d(TAG, "http res.write done");
                        fail("http res.write done");
                    }

                });

                res.end(null, null, null);

            }

        });

        srv.listen(port, "0.0.0.0", 1, new HttpServer.ListeningCallback() {

            @Override
            public void onListening() throws Exception {
                Log.d(TAG, "http server listening on " + port);
            }

        });

        // client
        final ReqOptions ropt = new ReqOptions();
        ropt.hostname = "localhost"; // IP address instead localhost
        ropt.port = port;
        ropt.method = "GET";
        ropt.path = "/";

        // defer 2s to connect
        ctx.setTimeout(new TimeoutListener() {

            @Override
            public void onTimeout() throws Exception {

                ClientRequest req = http.request(ctx, ropt, new ClientRequest.responseListener() {

                    @Override
                    public void onResponse(IncomingMessage res) throws Exception {
                        Log.d(TAG, "STATUS: " + res.statusCode());
                        Log.d(TAG, "HEADERS: " + res.getHeaders());

                        res.setEncoding("utf-8");

                        res.on("data", new Listener() {

                            @Override
                            public void onEvent(Object chunk) throws Exception {
                                Log.d(TAG, "BODY: " + chunk);

                            }

                        });
                    }

                });

                req.on("error", new Listener() {

                    @Override
                    public void onEvent(Object e) throws Exception {
                        Log.d(TAG, "problem with request: " + e);
                        fail("problem with request: " + e);
                    }

                });

                // write data to request body
                ///req.write("data\n", "utf-8", null);
                ///req.write("data\n", "utf-8", null);
                req.end(null, null, null);
            }

        }, 2000);

    }


    @Before
    public void setUp() throws Exception {
        this.ctx = new NodeContext();
    }
}