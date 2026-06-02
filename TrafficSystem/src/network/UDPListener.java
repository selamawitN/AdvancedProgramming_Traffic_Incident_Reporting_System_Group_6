package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPListener implements Runnable {

    private static final int UDP_PORT = 6000;
    private static final int BUFFER_SIZE = 1024;
    private boolean running = true;

    public interface AlertHandler {
        void onAlertReceived(String message);
    }

    private AlertHandler handler;

    public UDPListener(AlertHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
            socket.setBroadcast(true);
            byte[] buffer = new byte[BUFFER_SIZE];

            System.out.println("[UDP] Listening for broadcasts on port "
                + UDP_PORT);

            while (running) {
                DatagramPacket packet =
                    new DatagramPacket(buffer, buffer.length);

               
                socket.receive(packet);

                String message = new String(
                    packet.getData(), 0, packet.getLength()
                );

                System.out.println("[UDP] Received broadcast: " + message);

          
                if (handler != null) {
                    handler.onAlertReceived(message);
                }
            }
        } catch (Exception e) {
            if (running) {
                System.out.println("[UDP] Listener error: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
    }
}
