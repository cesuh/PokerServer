package serverNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import poker.Player;

public class GameConnection extends Connection {

	private Player player;
	private String message;
	private GameServer server;

	public GameConnection(DataOutputStream out, DataInputStream in, Player player, GameServer server) {
		super(out, in);
		this.player = player;
		this.server = server;
	}

	public final Player getPlayer() {
		return player;
	}

	public final String getMessage() {
		return message;
	}

	public final void deleteMessage() {
		message = null;
	}

	private void sendChatMessage(String message) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
		LocalDateTime now = LocalDateTime.now();
		server.broadcastMessage("CHATMESSAGE " + dtf.format(now).toString() + " " + message);
		message = null;
	}

	public void run() {
		while (true) {
			try {
				message = in.readUTF();
			} catch (IOException e) {
				out = null;
				in = null;
			}
			if (message != null) {
				String[] messageWords = message.split(" ");
				if (messageWords[0].equals("SETNAME") && messageWords.length > 1)
					player.setName(messageWords[1]);
				else if (messageWords[0].equals("CHATMESSAGE"))
					sendChatMessage(player.getName() + ": " + message.replaceAll("CHATMESSAGE", ""));
			}
		}
	}
}
