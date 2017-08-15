package poker;

import serverNetwork.GameConnection;

public class Player implements Comparable<Player> {

	private int stack;
	private Hand hand;
	private String playerName;
	private int tablePosition;
	private int bet;
	private HandValue handValue;
	private boolean hasMadeDecision;
	private GameConnection gc;

	public Player(int bankRoll, String playerName, int tablePosition, GameConnection gc) {
		this.hand = new Hand(null, null);
		this.stack = bankRoll;
		this.playerName = playerName;
		this.tablePosition = tablePosition;
		bet = 0;
		handValue = null;
		hasMadeDecision = false;
		this.gc = gc;
	}

	public GameConnection getGameConnection(){
		return gc;
	}
	
	public void setName(String name) {
		this.playerName = name;
	}

	public HandValue getHandValue() {
		return handValue;
	}

	public boolean getHasMadeDecision() {
		return hasMadeDecision;
	}

	public void setDecision(boolean decision) {
		hasMadeDecision = decision;
	}

	public void setHandValue(HandValue handValue) {
		this.handValue = handValue;
	}

	public void setBet(int bet) {
		this.bet = bet;
	}

	public int getBet() {
		return bet;
	}

	public void setTablePosition(int pos) {
		this.tablePosition = pos;
	}

	public int getTablePosition() {
		return tablePosition;
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getStack() {
		return stack;
	}

	public void setStack(int bankRoll) {
		if (bankRoll >= 0)
			this.stack = bankRoll;
	}

	public String getName() {
		return playerName;
	}

	public int bet(int bet) {
		stack -= bet;
		this.bet += bet;
		return this.bet;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public Hand getHand() {
		return hand;
	}

	@Override
	public int compareTo(Player o) {
		return handValue.compareTo(o.handValue);
	}
}
