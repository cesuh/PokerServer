package serverNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import poker.Player;

public class GameConnection implements Runnable, Comparable<GameConnection> {

	private Player player;
	private DataOutputStream out;
	private DataInputStream in;
	private String message;
	private GameServer server;

	public GameConnection(DataOutputStream out, DataInputStream in, Player player, GameServer server) {
		this.out = out;
		this.in = in;
		this.message = null;
		this.player = player;
		this.server = server;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public DataOutputStream getOutput() {
		return out;
	}

	public void deleteMessage() {
		message = null;
	}

	public String getMessage() {
		return message;
	}

	public void sendMessage(String message) {
		try {
			out.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendChatMessage(String message) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		for (Player p : server.getPlayers()) {
			p.getGameConnection().sendMessage("CHATMESSAGE " + dtf.format(now).toString() + " " + message);
			p.getGameConnection().deleteMessage();
		}
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
				String temp = message;
				String[] messageWords = message.split(" ");
				if (messageWords[0].equals("SETNAME")) {
					if (messageWords.length > 1)
						player.setName(messageWords[1]);
				} else if (messageWords[0].equals("CHATMESSAGE")) {
					sendChatMessage(player.getName() + ": " + temp.replaceAll("CHATMESSAGE", ""));
				}
			}
		}
	}

	@Override
	public int compareTo(GameConnection o) {
		return this.getPlayer().compareTo(o.getPlayer());
	}

}
