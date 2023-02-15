import java.io.*;
import java.net.*;

class SimpleWebClient {
	public static void main(String[] args) throws IOException {
		final int PORTNR = 8080;

		ServerSocket server = new ServerSocket(PORTNR);
		Socket connection = server.accept();
		PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);

		InputStreamReader readConnection = new InputStreamReader(connection.getInputStream());
		BufferedReader reader = new BufferedReader(readConnection);
		
		writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/html; charset=utf-8");
        writer.println("\r\n");
		writer.println("<html><body><h1>Hi. You are connected to my simple web-server.</h1>");

		String line = reader.readLine();
		while (!line.isBlank()) {
			writer.println("<ul><li>" + line + "</ul></li>");
			line = reader.readLine();
		}
		writer.println("</body></html>");
			
		writer.close();
		connection.close();
		server.close();
	}
}
