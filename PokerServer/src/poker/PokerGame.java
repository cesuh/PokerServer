package poker;

import java.util.ArrayList;

public class PokerGame {

	private Deck deck;
	private Table table;
	private int smallBlindValue;
	private int bigBlindValue;
	private int gameState;
	private int potSize;
	private int currentBet;
	private int startingStack;
	private Player dealer;
	private Player smallBlind;
	private Player bigBlind;
	private Player currentTurn;
	private Player winner;
	private ArrayList<Player> remainingPlayersInRound;
	private ArrayList<Card> board;

	public Player getWinner() {
		return winner;
	}

	public ArrayList<Card> getBoard() {
		return board;
	}

	public int getStartingStack() {
		return startingStack;
	}

	public int getGameState() {
		return gameState;
	}

	public int getPotSize() {
		return potSize;
	}

	public int getRemainingPlayersInGame() {
		return table.getPlayerList().size();
	}

	public int getRemainingPlayersInRound() {
		return remainingPlayersInRound.size();
	}

	public int getCurrentBet() {
		return currentBet;
	}

	public int getBigBlindValue() {
		return bigBlindValue;
	}

	public int getSmallBlindValue() {
		return smallBlindValue;
	}

	public ArrayList<Player> getRemainingPlayerList() {
		return table.getPlayerList();
	}

	public ArrayList<Player> getRemainingPlayersInRoundList() {
		return remainingPlayersInRound;
	}

	public Player getCurrentTurn() {
		return currentTurn;
	}

	public Player getDealer() {
		return dealer;
	}

	public Player getSmallBlind() {
		return smallBlind;
	}

	public Player getBigBlind() {
		return bigBlind;
	}

	public void check() {
		currentTurn.setDecision(true);
		updateCurrentTurn();
	}

	public void bet(int betSize) {
		int temp = currentTurn.getBet();
		int bet = currentTurn.bet(betSize);
		if (bet > currentBet)
			currentBet = bet;
		potSize += (bet - temp);
		currentTurn.setDecision(true);
		updateCurrentTurn();
	}

	public void fold() {
		Player temp = currentTurn;
		updateCurrentTurn();
		remainingPlayersInRound.remove(temp);

	}

	public void dealFlop() {
		for (int i = 0; i < 3; i++)
			board.add(deck.dealCard());
		gameState = 1;
		setDecisionsFalse();
		clearBets();
		currentTurn = table.getClockwisePlayer(dealer, remainingPlayersInRound);
		setHandValue();
	}

	public void dealTurn() {
		board.add(deck.dealCard());
		gameState = 2;
		setDecisionsFalse();
		clearBets();
		currentTurn = table.getClockwisePlayer(dealer, remainingPlayersInRound);
		setHandValue();
	}

	public void dealRiver() {
		board.add(deck.dealCard());
		gameState = 3;
		setDecisionsFalse();
		clearBets();
		currentTurn = table.getClockwisePlayer(dealer, remainingPlayersInRound);
		setHandValue();
	}

	public void showdown() {
		setHandValue();
		remainingPlayersInRound.sort(null);
		winner = remainingPlayersInRound.get(0);
		winner.setStack(winner.getStack() + potSize);
	}

	public PokerGame(ArrayList<Player> playerList) {
		gameState = 0;
		smallBlindValue = 15;
		bigBlindValue = 30;
		startingStack = 3000;
		table = new Table(6, new ArrayList<Player>());
		remainingPlayersInRound = new ArrayList<Player>();
		for (Player c : playerList)
			table.addPlayer(c);
		remainingPlayersInRound.addAll(playerList);
		dealer = table.getPlayer(0);
		updateBlinds();
		board = new ArrayList<Card>();
		deck = new Deck();
		deck.shuffle();
		dealCards();
		postBlinds();
	}

	public boolean agreementCheck() {
		for (Player p : remainingPlayersInRound)
			if ((p.getBet() != currentBet || !p.getHasMadeDecision()) && p.getStack() > 0)
				return false;
		return true;
	}

	public void startNewRound() {
		ArrayList<Player> temp = new ArrayList<Player>();
		temp.addAll(table.getPlayerList());
		for (Player p : temp)
			if (p.getStack() == 0) {
				table.removePlayer(p);
			}
		currentBet = 0;
		potSize = 0;
		gameState = 0;
		remainingPlayersInRound.clear();
		remainingPlayersInRound.addAll(table.getPlayerList());
		board = new ArrayList<Card>();
		deck = new Deck();
		deck.shuffle();
		dealCards();
		clearBets();
		updateDealerAndBlinds();
		setDecisionsFalse();
		postBlinds();
	}

	private void setDecisionsFalse() {
		for (Player p : remainingPlayersInRound)
			p.setDecision(false);
	}

	private void clearBets() {
		for (Player p : remainingPlayersInRound) {
			p.setBet(0);
		}
		currentBet = 0;
	}

	private void dealCards() {
		for (Player p : table.getPlayerList()) {
			p.getHand().setLeft(deck.dealCard());
			p.getHand().setRight(deck.dealCard());
		}
	}

	private void updateCurrentTurn() {
		currentTurn = table.getClockwisePlayer(currentTurn, remainingPlayersInRound);
	}

	private void postBlinds() {
		potSize += smallBlind.bet(smallBlindValue);
		potSize += bigBlind.bet(bigBlindValue);
		currentBet = bigBlindValue;
	}

	private void updateDealerAndBlinds() {
		dealer = table.getClockwisePlayer(dealer);
		updateBlinds();
	}

	private void updateBlinds() {
		smallBlind = table.getClockwisePlayer(dealer);
		bigBlind = table.getClockwisePlayer(smallBlind);
		currentTurn = table.getClockwisePlayer(bigBlind);
	}

	private void setHandValue() {
		for (Player p : remainingPlayersInRound) {
			p.setHandValue(new HandValue(p.getHand(), board));
		}
	}
}