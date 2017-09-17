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
	private boolean isStillInRound;
	private boolean isAllIn;
	private GameConnection gc;

	public Player(int bankRoll, String playerName, int tablePosition) {
		this.hand = new Hand(null, null);
		this.stack = bankRoll;
		this.playerName = playerName;
		this.tablePosition = tablePosition;
		bet = 0;
		handValue = null;
		hasMadeDecision = false;
		isAllIn = false;
	}

	public boolean isStillInRound() {
		return isStillInRound;
	}

	public boolean isAllIn() {
		return isAllIn;
	}
	
	public void setAllIn(boolean bool) {
		isAllIn = bool;
	}

	public final void setGameConnection(GameConnection gc) {
		this.gc = gc;
	}

	public final GameConnection getGameConnection() {
		return gc;
	}

	public void setStillInRound(boolean isStillInRound) {
		this.isStillInRound = isStillInRound;
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

	public int bet(int size) {
		stack -= size;
		bet = size;
		hasMadeDecision = true;
		return size;
	}

	public int callOrRaise(int size) {
		int newSize = size - bet;
		if (newSize >= stack) {
			newSize = stack;
			isAllIn = true;
		}
		stack -= newSize;
		bet += newSize;
		hasMadeDecision = true;
		return newSize;
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
