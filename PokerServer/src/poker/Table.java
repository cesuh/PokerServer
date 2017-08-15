package poker;

import java.util.ArrayList;

public class Table {

	private int tableSize;
	private ArrayList<Player> playerList;

	public Table(int tableSize, ArrayList<Player> playerList) {
		this.tableSize = tableSize;
		this.playerList = playerList;
	}

	public Player getPlayer(int pos) {
		return playerList.get(pos);
	}

	public void addPlayer(Player c) {
		if (playerList.size() < tableSize)
			playerList.add(c);
	}

	public boolean removePlayer(Player c) {
		return playerList.remove(c);
	}

	public ArrayList<Player> getPlayerList() {
		return playerList;
	}

	public Player getClockwisePlayer(Player c, ArrayList<Player> playerList) {
		
		if (c.equals(playerList.get(playerList.size()-1)))
			return playerList.get(0);
		else
			return playerList.get(playerList.indexOf(c)+1);
	}

	public Player getClockwisePlayer(Player c) {
		return getClockwisePlayer(c, playerList);
	}
}
