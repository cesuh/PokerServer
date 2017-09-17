package serverNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Connection implements Runnable {

	protected DataOutputStream out;
	protected DataInputStream in;

	public Connection(DataOutputStream out, DataInputStream in) {
		if (out != null && in != null) {
			this.out = out;
			this.in = in;
		}
	}

	protected final void sendMessage(String message) {
		if(message != null)
		try {
			out.writeUTF(message);
		} catch (IOException e) {
			System.out.println(e + "Failed to write to socket");
		}
	}

	protected final int parseIncomingMessageNumber(String messageNumber) {
		int number = -1;
		try {
			number = Integer.parseInt(messageNumber);
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			System.out.println("Error parsing integer. Input = " + messageNumber + "\n" + e);
		}
		return number;
	}

}
