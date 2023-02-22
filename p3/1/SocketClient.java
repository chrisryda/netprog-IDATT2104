import java.io.*;
import java.net.*;
import java.util.Scanner;

class SocketClient {
	public static void main(String[] args) throws IOException {
		final int PORTNR = 1250;
		Socket connection = new Socket("localhost", PORTNR);
		System.out.println("Connection established.");
		
		Scanner readFromTerminal = new Scanner(System.in);
		InputStreamReader readStream = new InputStreamReader(connection.getInputStream());
		BufferedReader reader = new BufferedReader(readStream);
		PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);

		String welcomeFromServer = reader.readLine();
		String welcomeFromServer2 = reader.readLine();
		System.out.println(welcomeFromServer + "\n" + welcomeFromServer2);

		String line = readFromTerminal.nextLine();
		while (!line.equals("")) {
			writer.println(line);
			String response = reader.readLine();
			System.out.println("From server: " + response);
			String result = reader.readLine();
			System.out.println(result);
			line = readFromTerminal.nextLine();
		}

		reader.close();
		writer.close();
		connection.close();
		readFromTerminal.close();
	}
}
