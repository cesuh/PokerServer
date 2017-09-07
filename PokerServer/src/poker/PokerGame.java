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

	public final Player getWinner() {
		return winner;
	}

	public final ArrayList<Card> getBoard() {
		return board;
	}

	public final int getStartingStack() {
		return startingStack;
	}

	public final int getGameState() {
		return gameState;
	}

	public final int getPotSize() {
		return potSize;
	}

	public final int getRemainingPlayersInGame() {
		return table.getPlayerList().size();
	}

	public final int getRemainingPlayersInRound() {
		return remainingPlayersInRound.size();
	}

	public final int getCurrentBet() {
		return currentBet;
	}

	public final int getBigBlindValue() {
		return bigBlindValue;
	}

	public final int getSmallBlindValue() {
		return smallBlindValue;
	}

	public final ArrayList<Player> getRemainingPlayerList() {
		return table.getPlayerList();
	}

	public final ArrayList<Player> getRemainingPlayersInRoundList() {
		return remainingPlayersInRound;
	}

	public final Player getCurrentTurn() {
		return currentTurn;
	}

	public final Player getDealer() {
		return dealer;
	}

	public final Player getSmallBlind() {
		return smallBlind;
	}

	public final Player getBigBlind() {
		return bigBlind;
	}

	public final void check() {
		currentTurn.setDecision(true);
		updateCurrentTurn();
	}

	public final void bet(int betSize) {
		int temp = currentTurn.getBet();
		int bet = currentTurn.bet(betSize);
		if (bet > currentBet)
			currentBet = bet;
		potSize += (bet - temp);
		currentTurn.setDecision(true);
		updateCurrentTurn();
	}

	public final void fold() {
		Player temp = currentTurn;
		updateCurrentTurn();
		remainingPlayersInRound.remove(temp);

	}

	public final void dealFlop() {
		for (int i = 0; i < 3; i++)
			board.add(deck.dealCard());
		gameState = 1;
		setDecisionsFalse();
		clearBets();
		currentTurn = table.getClockwisePlayer(dealer, remainingPlayersInRound);
		setHandValue();
	}

	public final void dealTurn() {
		board.add(deck.dealCard());
		gameState = 2;
		setDecisionsFalse();
		clearBets();
		currentTurn = table.getClockwisePlayer(dealer, remainingPlayersInRound);
		setHandValue();
	}

	public final void dealRiver() {
		board.add(deck.dealCard());
		gameState = 3;
		setDecisionsFalse();
		clearBets();
		currentTurn = table.getClockwisePlayer(dealer, remainingPlayersInRound);
		setHandValue();
	}

	public final void showdown() {
		setHandValue();
		remainingPlayersInRound.sort(null);
		winner = remainingPlayersInRound.get(0);
		winner.setStack(winner.getStack() + potSize);
	}

	public  PokerGame(ArrayList<Player> playerList) {
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