import java.io.*;
import java.net.*;

class SocketServerThreaded {
	public static void main(String[] args) throws IOException {
		final int PORTNR = 1250;
		ServerSocket server = new ServerSocket(PORTNR);
		System.out.println("Server ready and waiting");
		while (true) {
			Socket socket = server.accept();
			new SocketThread(socket).start();
		}
	}
}

class SocketThread extends Thread {
	
	private Socket threadSocket;
	SocketThread(Socket socket) {
		this.threadSocket = socket;
	}

	public void run() {
		try {
			System.out.println("New thread ready and waiting.");	
			InputStreamReader readConnection = new InputStreamReader(this.threadSocket.getInputStream());
			BufferedReader reader = new BufferedReader(readConnection);
			PrintWriter writer = new PrintWriter(this.threadSocket.getOutputStream(), true);
	
			writer.println("You are now connected to the server.");
			writer.println("Write an expression adding or subtracting two numbers seperated by space, e.g. '1 + 1', and I will send the result back to you.");
	
			String expression = reader.readLine();
			while (expression != null) {
				System.out.println("A client wrote: " + expression);
				writer.println("Expression recieved.");
				double result = calculate(expression);
				writer.println("Result of calculation: " + result);
				expression = reader.readLine();
			}
	
			reader.close();
			writer.close();
			this.threadSocket.close();

		} catch (IOException e) {
			System.out.println("Something went horribly wrong and an IOExeption occured.");
		}
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
