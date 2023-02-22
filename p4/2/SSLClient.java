import javax.net.ssl.SSLSocketFactory;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.net.Socket;

public class SSLClient {
    static final int port = 8000;

    public static void main(String[] args) {

        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        
        try {
            Socket socket = sslSocketFactory.createSocket("localhost", port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                Scanner scanner = new Scanner(System.in);
                while(true){
                    System.out.println("\nEnter something:");
                    String inputLine = scanner.nextLine();
                    out.println(inputLine);
                    if (inputLine.equals("q")) {break;}
                    System.out.println("From server: " + bufferedReader.readLine());
                }
            }   
        } catch (IOException ex) {
            Logger.getLogger(SSLClient.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
}
