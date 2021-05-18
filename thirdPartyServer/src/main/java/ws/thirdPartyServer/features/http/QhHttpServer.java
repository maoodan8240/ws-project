package ws.thirdPartyServer.features.http;

import org.apache.http.ExceptionLogger;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;

import java.util.concurrent.TimeUnit;

public class QhHttpServer {

    public static void create() throws Exception {
        int port = 8080;
        IOReactorConfig config = IOReactorConfig.custom().setSoTimeout(15000).setTcpNoDelay(true).build();
        final HttpServer server = ServerBootstrap.bootstrap() //
                .setListenerPort(port)//
                .setServerInfo("Test/1.1")//
                .setIOReactorConfig(config)//
                .setExceptionLogger(ExceptionLogger.STD_ERR)//
                .registerHandler("*", new PlatformRequestHandler()).create();//

        server.start();
        server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.shutdown(5, TimeUnit.SECONDS);
            }
        });
    }
}
