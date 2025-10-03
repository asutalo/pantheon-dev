package com.eu.atit.pantheon.server;

import com.eu.atit.pantheon.server.endpoint.EndpointProcessor;
import com.eu.atit.pantheon.server.endpoint.IoCEndpoint;
import com.eu.atit.pantheon.server.endpoint.Registry;
import com.eu.atit.pantheon.server.request.Handler;
import com.eu.atit.pantheon.server.request.parsing.ParsingService;
import com.eu.atit.pantheon.server.request.validation.Validator;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    static final int INSTANT_STOP = 0;
    private final int port;
    private final int coreConnectionPoolSize;
    private final int queueSize;
    private final long connectionIdleTime;
    private final TimeUnit unit;
    private final boolean coreThreadTimeout;
    private final Validator validator;
    private final ParsingService parsingService;
    private final Registry registry;
    private final EndpointProcessor endpointProcessor;
    private HttpServer httpServer;
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * @param port                   the configured port where requests will be served
     * @param coreConnectionPoolSize the number of connections that can be handled simultaneously
     * @param queueSize              the maximum allowed number of connection handlers after the simultaneous capacity is reached
     * @param connectionIdleTime     when the number of connections is greater than the simultaneous capacity,
     *                               this is the maximum time that excess idle connection handlers will wait before terminating.
     * @param unit                   the time unit for the connectionIdleTime argument
     * @param coreThreadTimeout      if true then the core, idle simultaneous connection handlers can terminate as well
     */
    public Server(int port, int coreConnectionPoolSize, int queueSize, long connectionIdleTime, TimeUnit unit, boolean coreThreadTimeout) throws IOException {
        this.port = port;
        this.coreConnectionPoolSize = coreConnectionPoolSize;
        this.queueSize = queueSize;
        this.connectionIdleTime = connectionIdleTime;
        this.unit = unit;
        this.coreThreadTimeout = coreThreadTimeout;
        this.threadPoolExecutor = threadPoolExecutor();
        this.httpServer = HttpServer.create();
        this.validator = new Validator();
        parsingService = ParsingService.getInstance();
        registry = new Registry();
        endpointProcessor = new EndpointProcessor();
    }

    /**
     * Starts the server and occupies the Main thread.
     * The server listens for incoming connections and handles them by passing them down to
     * {@link Handler} which will parse and execute the request.
     */
    public void start(InetSocketAddress socketAddress) throws IOException {
        httpServer.bind(socketAddress, queueSize);
        httpServer.createContext("/", new Handler(validator, parsingService, registry, endpointProcessor));
        httpServer.setExecutor(threadPoolExecutor);
        httpServer.start();
    }

    public void start() throws IOException {
        start(new InetSocketAddress(port));
    }

    public void startLocalhost() throws IOException {
        start(new InetSocketAddress("localhost", port));
    }

    public void registerEndpoint(IoCEndpoint endpoint) {
        registry.registerEndpoint(endpoint);
    }

    ThreadPoolExecutor threadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(coreConnectionPoolSize, getMaximumPoolSize(), connectionIdleTime, unit, new SynchronousQueue<>());
        threadPoolExecutor.allowCoreThreadTimeOut(coreThreadTimeout);

        return threadPoolExecutor;
    }

    private int getMaximumPoolSize() {
        return coreConnectionPoolSize + queueSize;
    }

    public void shutDown() {
        shutDown(INSTANT_STOP);
    }

    /**
     * Attempt a graceful shutdown after the specified timeout period
     *
     * @param timeout amount of time to await termination of all running threads
     */
    public void shutDown(int timeout) {
        httpServer.stop(timeout);
    }

    //For tests
    HttpServer getHttpServer() {
        return httpServer;
    }

    //For tests
    void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    //For tests
    ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    //For tests
    void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }
}
