package serverNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class LobbyServer extends Server {

	private ArrayList<GameServer> serverList;

	public LobbyServer() throws IOException {
		super();
		serverList = new ArrayList<GameServer>();
	}

	public GameServer createNewGameServer(int numberOfPlayers) throws IOException {
		GameServer game = new GameServer(numberOfPlayers);
		new Thread(game).start();
		serverList.add(game);
		return game;
	}

	public final ArrayList<GameServer> getServerList() {
		return serverList;
	}

	@Override
	public void run() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				out = new DataOutputStream(socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				System.out.println("Failed to accept connection from client " + e);
			}
			LobbyConnection connection = new LobbyConnection(out, in, this);
			connections.add(connection);
			new Thread(connection).start();
		}
	}
}
