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
	private ArrayList<Player> allInPlayersThisRound;
	private ArrayList<Card> board;
	private boolean playersAllInState;

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

	public final int getAllInPlayersSize() {
		return allInPlayersThisRound.size();
	}

	public final boolean getPlayersAllInState() {
		return playersAllInState;
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
		int bet = currentTurn.bet(betSize);
		currentBet = bet;
		potSize += bet;
		updateCurrentTurn();
	}

	public final void call() {
		int size = currentTurn.callOrRaise(currentBet);
		potSize += size;
		Player temp = currentTurn;
		if (remainingPlayersInRound.size() > 1)
			updateCurrentTurn();
		if (remainingPlayersInRound.size() == 1 && allInPlayersThisRound.size() >= 1)
			playersAllInState = true;
	}

	public final void raise(int raiseSize) {
		int tempBet = currentTurn.getBet();
		int size = currentTurn.callOrRaise(raiseSize);
		currentBet = size + tempBet;
		potSize += size;
		updateCurrentTurn();
	}

	public final void fold() {
		Player temp = currentTurn;
		updateCurrentTurn();
		remainingPlayersInRound.remove(temp);
		if (allInPlayersThisRound.size() > 1 && remainingPlayersInRound.isEmpty())
			playersAllInState = true;
		else if (allInPlayersThisRound.size() >= 1 && remainingPlayersInRound.size() == 1) {
			boolean allInState = true;
			for (Player p : allInPlayersThisRound)
				if (p.getBet() > remainingPlayersInRound.get(0).getBet())
					allInState = false;
			playersAllInState = allInState;
		}
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
		remainingPlayersInRound.addAll(allInPlayersThisRound);
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
		allInPlayersThisRound = new ArrayList<Player>();
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
			if (p.getBet() != currentBet || !p.getHasMadeDecision())
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
		for (int i = 0; i < table.getPlayerList().size(); i++)
			table.getPlayer(i).setTablePosition(i);
		currentBet = 0;
		potSize = 0;
		gameState = 0;
		playersAllInState = false;
		remainingPlayersInRound.clear();
		allInPlayersThisRound.clear();
		remainingPlayersInRound.addAll(table.getPlayerList());
		board = new ArrayList<Card>();
		deck = new Deck();
		deck.shuffle();
		setPlayersNotAllIn();
		setStillInRound();
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
		Player temp = currentTurn;
		currentTurn = table.getClockwisePlayer(currentTurn, remainingPlayersInRound);
		if (temp.isAllIn()) {
			remainingPlayersInRound.remove(temp);
			allInPlayersThisRound.add(temp);
		}
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

	private void setStillInRound() {
		for (Player p : remainingPlayersInRound)
			p.setStillInRound(true);
	}

	private void setPlayersNotAllIn() {
		for (Player p : remainingPlayersInRound)
			p.setAllIn(false);
	}

	private void setHandValue() {
		for (Player p : remainingPlayersInRound) {
			p.setHandValue(new HandValue(p.getHand(), board));
		}
	}
}