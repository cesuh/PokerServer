package serverNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public abstract class Server implements Runnable {

	protected final int MIN_PORT_NUMBER = 1100;
	protected final int MAX_PORT_NUMBER = 65535;
	protected int PORT_NUMBER;
	protected ServerSocket serverSocket;
	protected DataOutputStream out;
	protected DataInputStream in;
	protected ArrayList<Connection> connections;
	protected int portCounter = MIN_PORT_NUMBER;

	public Server() throws IOException {
		boolean cont = true;
		while (cont) {
			if (available(portCounter)) {
				serverSocket = new ServerSocket(portCounter);
				PORT_NUMBER = portCounter;
				cont = false;
				if (portCounter == MAX_PORT_NUMBER)
					portCounter = MIN_PORT_NUMBER;
				else
					portCounter++;
			} else
				portCounter++;
		}
		connections = new ArrayList<Connection>();
	}
	
	protected final int getPortNumber() {
		return PORT_NUMBER;
	}
	
	protected Connection getConnection() {
		return connections.get(0);
	}
	
	protected void declareArrayList(ArrayList<Connection> list) {
		this.connections = list;
	}
	
	protected void broadcastMessage(String message) {
		if(message != null)
		for(Connection c : connections)
			c.sendMessage(message);
	}

	private final boolean available(int port) {
		if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}
		return false;
	}
}
