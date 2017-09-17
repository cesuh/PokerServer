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

	public void addPlayer(Player p) {
		if (playerList.size() < tableSize)
			playerList.add(p);
	}

	public boolean removePlayer(Player p) {
		return playerList.remove(p);
	}

	public ArrayList<Player> getPlayerList() {
		return playerList;
	}

	public Player getClockwisePlayer(Player p, ArrayList<Player> playerList) {
		if (p.equals(playerList.get(playerList.size()-1)))
			return playerList.get(0);
		else
			return playerList.get(playerList.indexOf(p)+1);
	}

	public Player getClockwisePlayer(Player p) {
		return getClockwisePlayer(p, playerList);
	}
}