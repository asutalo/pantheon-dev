package com.eu.atit.pantheon.server;

import com.eu.atit.pantheon.server.endpoint.Endpoint;
import com.eu.atit.pantheon.server.endpoint.Registry;
import com.eu.atit.pantheon.server.request.Handler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.eu.atit.pantheon.server.Server.INSTANT_STOP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServerTest {
    private static final int SOME_PORT = 80;
    private static final int SOME_CORE_CONNECTION_POOL_SIZE = 50;
    private static final int SOME_QUEUE_SIZE = 85;
    private static final long SOME_CONNECTION_IDLE_TIME = 90L;
    private static final int SOME_SHUTDOWN_TIME = 90;
    private static final TimeUnit SOME_UNITS = TimeUnit.SECONDS;
    private static final boolean SOME_CORE_THREAD_TIMEOUT = true;
    private Server server;

    @Mock
    private HttpServer mockHttpServer;

    @Mock
    private ThreadPoolExecutor mockThreadPoolExecutor;


    @BeforeEach
    void setUp() throws IOException {
        server = new Server(SOME_PORT, SOME_CORE_CONNECTION_POOL_SIZE, SOME_QUEUE_SIZE, SOME_CONNECTION_IDLE_TIME, SOME_UNITS, SOME_CORE_THREAD_TIMEOUT);
    }

    @DisplayName("starting")
    @Nested
    class StartTesting {
        @Test
        void start_shouldSetCorrectExecutor() throws IOException {
            server.setHttpServer(mockHttpServer);
            ThreadPoolExecutor expectedExecutor = server.getThreadPoolExecutor();

            server.start();

            verify(mockHttpServer).setExecutor(eq(expectedExecutor));
        }

        @Test
        void start_shouldSetCorrectContext() throws IOException {
            server.setHttpServer(mockHttpServer);

            server.start();
            verify(mockHttpServer).createContext(eq("/"), any(Handler.class));
        }

        @Test
        void start_shouldBindCorrectly() throws IOException {
            server.setHttpServer(mockHttpServer);

            server.start();
            verify(mockHttpServer).bind(eq(new InetSocketAddress("localhost", SOME_PORT)), eq(SOME_QUEUE_SIZE));
        }

        @Test
        void start_shouldStartServer() throws IOException {
            server.setHttpServer(mockHttpServer);

            server.start();
            verify(mockHttpServer).start();
        }
    }

    @DisplayName("initialisation")
    @Nested
    class InitialisationTesting {
        @Test
        void server_shouldInitialiseThreadPoolExecutor() {
            ThreadPoolExecutor actual = server.threadPoolExecutor();

            assertNotNull(actual);
            assertEquals(SOME_CORE_CONNECTION_POOL_SIZE, actual.getCorePoolSize());
            assertEquals(SOME_CORE_CONNECTION_POOL_SIZE + SOME_QUEUE_SIZE, actual.getMaximumPoolSize());
            assertEquals(SOME_CONNECTION_IDLE_TIME, actual.getKeepAliveTime(SOME_UNITS));
            assertEquals(SynchronousQueue.class, actual.getQueue().getClass());
            assertEquals(SOME_CORE_THREAD_TIMEOUT, actual.allowsCoreThreadTimeOut());

        }

        @Test
        void server_shouldInitialiseHttpServer() {
            assertNotNull(server.getHttpServer());
        }

        @Test
        void register_shouldPassEndpointToRegistry() throws NoSuchFieldException, IllegalAccessException {
            Endpoint mockEndpoint = mock(Endpoint.class);
            Registry mockRegistry = mock(Registry.class);

            Field registryField = server.getClass().getDeclaredField("registry");
            registryField.setAccessible(true);
            registryField.set(server, mockRegistry);

            server.registerEndpoint(mockEndpoint);

            verify(mockRegistry).registerEndpoint(mockEndpoint);
        }
    }

    @DisplayName("shutdown")
    @Nested
    class ShutDownTesting {
        @DisplayName("instantly shutting the server down")
        @Test
        void shutdown_shouldShutTheExecutorDown() {
            server.setHttpServer(mockHttpServer);
            server.setThreadPoolExecutor(mockThreadPoolExecutor);

            server.shutDown();

            verify(mockThreadPoolExecutor, never()).shutdown();
            verify(mockHttpServer).stop(INSTANT_STOP);
        }

        @DisplayName("gracefully shutting the server down")
        @Nested
        class GracefulShutDownTesting {

            @Test
            void shutdown_shouldAwaitTerminationAndShutTheExecutorDown() throws InterruptedException {
                server.setHttpServer(mockHttpServer);
                server.setThreadPoolExecutor(mockThreadPoolExecutor);

                server.shutDown(SOME_SHUTDOWN_TIME);

                verify(mockThreadPoolExecutor, never()).awaitTermination(anyLong(), any());
                verify(mockHttpServer).stop(SOME_SHUTDOWN_TIME);
            }

        }
    }
}