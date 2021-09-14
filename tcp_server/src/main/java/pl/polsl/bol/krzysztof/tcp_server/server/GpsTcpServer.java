package pl.polsl.bol.krzysztof.tcp_server.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.polsl.bol.krzysztof.tcp_server.service.GpsTcpServerService;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
@Service
public class GpsTcpServer {

    private final GpsTcpServerService service;

    @Value("${gps.tcp-server-port}")
    private int server_port;

    private ServerSocket serverSocket;

    public GpsTcpServer(final GpsTcpServerService service) {
        this.service = service;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        try {
            this.serverSocket = new ServerSocket(server_port);
            log.info("Starting server on port " + server_port + ". Waiting for clients...");
            while (true) {
                new GpsTcpServerThread(this.serverSocket.accept(), this).start();
            }
        } catch (final IOException e) {
            log.error("IOException thrown during starting process. Message: " + e.getLocalizedMessage());
        }

    }

    public void save(final String data) {
        this.service.save(data);
    }
}
