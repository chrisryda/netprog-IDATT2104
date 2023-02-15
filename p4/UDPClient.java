import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    public static void main(String[] args) throws IOException {
        
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        InetAddress address = InetAddress.getByName("localhost");
        DatagramSocket datagramSocket = null;

        String msg = "init"; 
        System.out.println("Write an expression adding, subtracting, dividing or multiplying two numbers seperated by space, e.g. '1 + 1'");
        System.out.println("Quit by sending an empty line.");
        System.out.println();
        while (!msg.isBlank()) {
            datagramSocket = new DatagramSocket();
            byte[] buf = new byte[1054];

            msg = input.readLine();
            System.arraycopy(msg.getBytes(), 0, buf, 0, msg.getBytes().length);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 8080);
            String sentMsg = new String(packet.getData());
            datagramSocket.send(packet);
            
            
            packet = new DatagramPacket(buf, buf.length, address, 8080);
            datagramSocket.receive(packet);
            System.out.printf("Sent: [%s]\nRecieved: [%s]", new String(sentMsg).trim(), new String(packet.getData()).trim());
            System.out.println("\n");
        }
        datagramSocket.close();
    }
}
