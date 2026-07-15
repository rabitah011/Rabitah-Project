package com.rabitah.frontend.api;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

public final class ServerDiscovery {
    private static final int PORT = 45871;
    private static final byte[] REQUEST = "RABITAH_DISCOVER_V1".getBytes(StandardCharsets.UTF_8);
    private ServerDiscovery() { }

    public static Optional<URI> discover(Duration timeout) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout((int) timeout.toMillis());
            Set<InetAddress> targets = new LinkedHashSet<>();
            targets.add(InetAddress.getLoopbackAddress());
            targets.add(InetAddress.getByName("255.255.255.255"));
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                if (!network.isUp() || network.isLoopback()) continue;
                for (InterfaceAddress address : network.getInterfaceAddresses())
                    if (address.getBroadcast() != null) targets.add(address.getBroadcast());
            }
            for (InetAddress target : targets)
                socket.send(new DatagramPacket(REQUEST, REQUEST.length, target, PORT));
            byte[] responseBytes = new byte[128];
            while (true) {
                DatagramPacket response = new DatagramPacket(responseBytes, responseBytes.length);
                socket.receive(response);
                String body = new String(response.getData(), response.getOffset(), response.getLength(), StandardCharsets.UTF_8);
                if (body.equals("RABITAH_SERVER_V1"))
                    return Optional.of(URI.create("http://" + response.getAddress().getHostAddress() + ":8080/api/v1/"));
            }
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
