package serverNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LobbyConnection extends Connection {

	private LobbyServer lobbyServer;

	public LobbyConnection(DataOutputStream out, DataInputStream in, LobbyServer lobbyServer) {
		super(out, in);
		if (lobbyServer != null)
			this.lobbyServer = lobbyServer;
	}

	public void run() {
		while (true) {
			String message = null;
			try {
				message = in.readUTF();
			} catch (IOException e) {
			}
			if (message != null) {
				ArrayList<GameServer> serverList = lobbyServer.getServerList();
				String[] incomingMessageWords = message.split(" ");

				if (incomingMessageWords[0].equals("REQUESTCREATEGAME")) {

					int numberOfPlayers = parseIncomingMessageNumber(incomingMessageWords[1]);

					if (numberOfPlayers > 1) {
						GameServer game = null;
						try {
							game = lobbyServer.createNewGameServer(numberOfPlayers);
						} catch (IOException e) {
							System.out.println("Failed to create new game. " + e);
						}
						if (game != null) {
							lobbyServer.broadcastMessage(
									"ADDGAMETOLIST " + 0 + " " + numberOfPlayers);
						}
					}
				}

				else if (incomingMessageWords[0].equals("REQUESTJOINGAME") && incomingMessageWords.length == 2) {

					int listPos = parseIncomingMessageNumber(incomingMessageWords[1]);
					if (serverList.size() > listPos && listPos >= 0) {
						GameServer gameServer = serverList.get(listPos);
						if (!gameServer.isFull())
							sendMessage("REQUESTJOINGAMEACCEPTED " + gameServer.getPortNumber());
					} else 
						sendMessage("REQUESTJOINGAMEDENIED");
					
				}
			}
		}
	}
}