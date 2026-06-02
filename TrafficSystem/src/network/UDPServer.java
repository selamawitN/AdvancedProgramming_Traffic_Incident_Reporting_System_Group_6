package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {

    private static final int UDP_PORT = 6000;
    private DatagramSocket socket;

    public UDPServer() {
        try {
            socket = new DatagramSocket();
            System.out.println("UDP Server ready on port " + UDP_PORT);
        } catch (Exception e) {
            System.out.println("UDP Server error: " + e.getMessage());
        }
    }

    public void broadcast(String message) {
        try {
            byte[] data = message.getBytes();

            InetAddress broadcastAddress =
                InetAddress.getByName("255.255.255.255");

            DatagramPacket packet = new DatagramPacket(
                data, data.length, broadcastAddress, UDP_PORT
            );

            socket.setBroadcast(true);
            socket.send(packet);

            System.out.println("[UDP] Broadcast sent: " + message);

        } catch (Exception e) {
            System.out.println("[UDP] Broadcast error: " + e.getMessage());
        }
    }

    public void sendTo(String ipAddress, String message) {
        try {
            byte[] data = message.getBytes();
            InetAddress address = InetAddress.getByName(ipAddress);
            DatagramPacket packet = new DatagramPacket(
                data, data.length, address, UDP_PORT
            );
            socket.send(packet);
            System.out.println("[UDP] Sent to " + ipAddress + ": " + message);
        } catch (Exception e) {
            System.out.println("[UDP] Send error: " + e.getMessage());
        }
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
