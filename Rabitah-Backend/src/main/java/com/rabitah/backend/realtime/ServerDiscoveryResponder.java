package com.rabitah.backend.realtime;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ServerDiscoveryResponder implements ApplicationRunner, AutoCloseable {
    private static final byte[] REQUEST = "RABITAH_DISCOVER_V1".getBytes(StandardCharsets.UTF_8);
    private static final byte[] RESPONSE = "RABITAH_SERVER_V1".getBytes(StandardCharsets.UTF_8);
    private final int discoveryPort;
    private volatile boolean running;
    private DatagramSocket socket;

    public ServerDiscoveryResponder(@Value("${rabitah.discovery.port:45871}") int discoveryPort) {
        this.discoveryPort = discoveryPort;
    }

    @Override public void run(ApplicationArguments args) {
        running = true;
        Thread.ofVirtual().name("rabitah-server-discovery").start(this::listen);
    }

    private void listen() {
        try (DatagramSocket responder = new DatagramSocket(discoveryPort)) {
            socket = responder;
            responder.setBroadcast(true);
            byte[] buffer = new byte[128];
            while (running) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                responder.receive(request);
                String body = new String(request.getData(), request.getOffset(), request.getLength(), StandardCharsets.UTF_8);
                if (body.equals(new String(REQUEST, StandardCharsets.UTF_8))) {
                    responder.send(new DatagramPacket(RESPONSE, RESPONSE.length, request.getAddress(), request.getPort()));
                }
            }
        } catch (Exception ignored) {
            if (running) System.err.println("Rabitah automatic server discovery is unavailable on UDP " + discoveryPort);
        }
    }

    @Override public void close() {
        running = false;
        if (socket != null) socket.close();
    }
}
