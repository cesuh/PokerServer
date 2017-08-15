package serverNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LobbyConnection implements Runnable {

	private DataOutputStream out;
	private DataInputStream in;
	private String message;
	private LobbyServer lobbyServer;

	public LobbyConnection(DataOutputStream out, DataInputStream in, LobbyServer lobbyServer) {
		this.out = out;
		this.in = in;
		this.message = null;
		this.lobbyServer = lobbyServer;
	}

	public void run() {
		while (true) {
			try {
				message = in.readUTF();
			} catch (IOException e) {
				this.out = null;
				this.in = null;
			}
			if (message != null) {

				ArrayList<GameServer> serverList = lobbyServer.getServerList();
				ArrayList<LobbyConnection> connections = lobbyServer.getConnections();
				String[] incomingMessageWords = message.split(" ");

				if (incomingMessageWords[0].equals("REQUESTCREATEGAME")) {
					String name = null;
					int numberOfPlayers = -1;
					try {
						name = incomingMessageWords[1];
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("Create game name index out of bounds = " + message);
					}

					try {
						numberOfPlayers = Integer.parseInt(incomingMessageWords[2]);
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("Create game number of players index out of bounds. Message = " + message);
					} catch (NumberFormatException e) {
						System.out.println(
								"Create game number of players unexpected type. Expected int. Message = " + message);
					}

					GameServer game = null;
					if (name != null && numberOfPlayers > 1)
						game = lobbyServer.createNewGameServer(name, numberOfPlayers);

					if (game != null)
						for (LobbyConnection c : connections)
							try {
								c.out.writeUTF("ADDGAMETOLIST " + name + " " + game.getNumberOfgamePlayers() + " "
										+ numberOfPlayers);
							} catch (IOException e) {
								System.out.println("Failed to write to socket");
							}
				}

				else if (incomingMessageWords[0].equals("REQUESTJOINGAME")) {

					int listPos = -1;

					try {
						listPos = Integer.parseInt(incomingMessageWords[1]);
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("Request join game listPos index out of bounds. Message = " + message);
					} catch (NumberFormatException e) {
						System.out.println(
								"Request join game listPos unexpected type. Expected int. Message = " + message);
					}
					GameServer server = null;
					if (serverList.size() > listPos)
						server = serverList.get(listPos);
					if (server != null) {
						if (server.getNumberOfgamePlayers() < server.getNumberOfPlayers())
							try {
								out.writeUTF("REQUESTJOINGAMEACCEPTED " + server.getPortNumber());
							} catch (IOException e) {
								System.out.println("Failed to write to socket");
							}
					} else
						try {
							out.writeUTF("REQUESTJOINGAMEDENIED");
						} catch (IOException e) {
							System.out.println("Failed to write to socket");
						}
				}
			}
		}
	}
}
