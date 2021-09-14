package pl.polsl.bol.krzysztof.tcp_server.server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Slf4j
public class GpsTcpServerThread extends Thread {

    private final Socket client;
    private final GpsTcpServer server;
    private BufferedReader reader;

    public GpsTcpServerThread(final Socket client, final GpsTcpServer server) {
        this.client = client;
        this.server = server;
    }

    @SneakyThrows(IOException.class)
    @Override
    public void run() {
        log.info("Client with IP address {} connected.", client.getInetAddress());
        this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        final StringBuilder sb = new StringBuilder();
        int value;
        while ((value = reader.read()) != -1) {
            final char character = (char) value;
            if (character == ')') {
                final String message = sb.toString();
                if (!message.isEmpty()) {
                    log.info("Message from client with IP address {}: {}.", client.getInetAddress(), message);
                    this.server.save(message);
                }
                sb.setLength(0);
            } else {
                if (!(character == '(' || character == ' ')) { // ignore beginning of the message
                    sb.append((char) value);

                }
            }
        }
        this.reader.close();
        this.client.close();
        log.info("Client with IP {} disconnected.", client.getInetAddress());
    }
}
