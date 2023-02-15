
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    public static void main(String[] args) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(8080);

        System.out.println("Server Start");
        String received = "init";
        
        while (!received.isBlank()) 
        {
            byte[] buf = new byte[1054];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            datagramSocket.receive(packet);
            received = new String(packet.getData()).trim();
            System.out.println("Received: " + received);

            double result = calculate(received);

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            String msg = "Result: " + result;
            System.arraycopy(msg.getBytes(), 0, buf, 0, msg.getBytes().length);
            packet = new DatagramPacket(buf, buf.length, address, port);
            datagramSocket.send(packet);
        }
        datagramSocket.close();
    }

    public static double calculate(String expression) {
        String[] data = expression.split(" ", 0);
        if (data.length != 3) {
            throw new IllegalArgumentException("Input is invalid.");
        }

        double a = Double.parseDouble(data[0]);
        double b = Double.parseDouble(data[2]);        
        String operation = data[1];

        double result;
        switch(operation) {
            case "+":
                result = a + b;
				break;	
            case "-":
                result = a - b;
				break;
            case "/":
                result = a / b;
                break;
            case "*":
                result = a * b;
                break;
            default:
                throw new IllegalArgumentException("Invalid operator");
        }
        return result;
    }
}
