package serverNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class LobbyServer implements Runnable {

	private ArrayList<GameServer> serverList;
	private ArrayList<LobbyConnection> connections;
	private ServerSocket serverSocket;
	private DataOutputStream out;
	private DataInputStream in;
	private int portNumber;
	private int portNumberCounter;

	public LobbyServer(int portNumber) {
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println("Failed to create lobby server socket");
		}
		connections = new ArrayList<LobbyConnection>();
		serverList = new ArrayList<GameServer>();
		this.portNumberCounter = 1101;
		this.portNumber = portNumber;
	}

	public GameServer createNewGameServer(String name, int numberOfPlayers) {
		GameServer game = new GameServer(name, numberOfPlayers, portNumberCounter++);
		new Thread(game).start();
		serverList.add(game);
		return game;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public ArrayList<GameServer> getServerList() {
		return serverList;
	}

	public ArrayList<LobbyConnection> getConnections() {
		return connections;
	}

	@Override
	public void run() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				in = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			LobbyConnection connection = new LobbyConnection(out, in, this);
			connections.add(connection);
			new Thread(connection).start();
		}
	}
}
