package serverNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import poker.Card;
import poker.Hand;
import poker.Player;
import poker.PokerGame;

public class GameServer extends Server {

	private int tablePosition;
	private boolean continueRound;
	private String[] incomingMessageWords;
	private Player currentTurn;
	private GameConnection currentTurnGC;
	private PokerGame game;
	private boolean isFull;
	private final int numberOfPlayers;
	private ArrayList<Player> players;

	public GameServer(int numberOfPlayers) throws IOException {
		super();
		this.numberOfPlayers = numberOfPlayers;
		isFull = false;
		players = new ArrayList<Player>();
	}

	public final int getNumberOfPlayers() {
		return players.size();
	}

	public final boolean isFull() {
		return isFull;
	}

	public final int getNumberOfConnections() {
		return connections.size();
	}

	public final int getNumberOfPlayersLeftInGame() {
		return players.size();
	}

	private final boolean validBetRaiseInput() {
		int size = 0;
		try {
			size = Integer.parseInt(incomingMessageWords[1]);
			if ((size >= game.getCurrentBet() + game.getBigBlindValue() || size == currentTurn.getStack())
					&& size <= currentTurn.getStack() + currentTurn.getBet())
				return true;
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			return false;
		}
		return false;
	}

	private final void initializeInterface() {
		for (Connection c : connections) {
			GameConnection gc = (GameConnection) c;
			Player p = gc.getPlayer();
			String[] words = gc.getMessage().split(" ");
			p.setName(words[1]);
			broadcastMessage("NAME " + p.getTablePosition() + " " + p.getName());
			broadcastMessage("PLAYERSTACK " + p.getTablePosition() + " " + game.getStartingStack());
			broadcastMessage("HIDELABEL");
		}
	}

	private void sleep(int duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			System.out.println("Failed to sleep thread");
		}
	}

	private void startNewRound() {
		Player sb = game.getSmallBlind();
		Player bb = game.getBigBlind();
		broadcastMessage("CLEARTABLE");
		sleep(500);
		broadcastMessage("BET " + sb.getTablePosition() + " " + sb.getBet() + " " + sb.getStack());
		sleep(500);
		broadcastMessage("BET " + bb.getTablePosition() + " " + bb.getBet() + " " + bb.getStack());
		sleep(500);
		broadcastMessage("DEALER " + game.getDealer().getTablePosition());
		broadcastMessage("UPDATEPOTSIZE " + game.getPotSize());
		for (Connection c : connections) {
			GameConnection gc = (GameConnection) c;
			Player p = gc.getPlayer();
			Hand hand = p.getHand();
			Card left = hand.getLeft();
			Card right = hand.getRight();
			c.sendMessage("DEALCARDS " + p.getTablePosition() + " " + left.getSuit() + " " + left.getRank() + " "
					+ right.getSuit() + " " + right.getRank() + " " + game.getRemainingPlayersInGame());
		}
	}

	private void clearPlayerBets() {
		broadcastMessage("CLEARBETS");
	}

	private void dealFlop() {
		game.dealFlop();
		Card one = game.getBoard().get(0);
		Card two = game.getBoard().get(1);
		Card three = game.getBoard().get(2);
		broadcastMessage("DEALFLOP " + one.getSuit() + " " + one.getRank() + " " + two.getSuit() + " " + two.getRank()
				+ " " + three.getSuit() + " " + three.getRank());
		clearPlayerBets();
	}

	private void dealTurn() {
		game.dealTurn();
		Card turn = game.getBoard().get(3);
		broadcastMessage("DEALTURN " + turn.getSuit() + " " + turn.getRank());
		clearPlayerBets();
	}

	private void dealRiver() {
		game.dealRiver();
		Card river = game.getBoard().get(4);
		broadcastMessage("DEALRIVER " + river.getSuit() + " " + river.getRank());
		clearPlayerBets();
	}

	private void showPlayerCards() {
		for (Player p : game.getRemainingPlayerList()) {
			Card left = p.getHand().getLeft();
			Card right = p.getHand().getRight();
			broadcastMessage("SHOWDOWN " + p.getTablePosition() + " " + left.getSuit() + " " + left.getRank() + " "
					+ right.getSuit() + " " + right.getRank() + " " + game.getRemainingPlayersInGame());
		}
	}

	private void showdown() {
		game.showdown();
		showPlayerCards();
		Player winner = game.getWinner();
		String tempName = winner.getName();
		int pos = winner.getTablePosition();
		broadcastMessage("NAME " + pos + " Winner");
		for (int i = 0; i < 10; i++) {
			broadcastMessage("CHANGEPLAYERBOXCOLOR " + pos);
			sleep(650);
		}
		continueRound = false;
		broadcastMessage("NAME " + pos + " " + tempName);
		game.startNewRound();
	}

	/**
	 * Sends a message to the client of the current turn. The message has this
	 * format "format"
	 */
	private void displayButtons() {
		currentTurnGC.sendMessage("DISPLAYBUTTONS " + currentTurn.getBet() + " " + game.getCurrentBet() + " "
				+ game.getBigBlindValue() + " " + currentTurn.getStack());
		broadcastMessage("SHOWPROGRESSBAR " + tablePosition);
	}

	private void fold() {
		broadcastMessage("FOLD " + tablePosition);
		game.fold();
		if (game.getPlayersAllInState()) {
			allInState();
		} else if (game.getRemainingPlayersInRound() < 2 && game.getAllInPlayersSize() == 0) {
			game.startNewRound();
			continueRound = false;
		}
		currentTurnGC.deleteMessage();
	}

	private void check() {
		broadcastMessage("CHECK " + tablePosition);
		game.check();
		currentTurnGC.deleteMessage();
	}

	private void call() {
		game.call();
		broadcastMessage("CALL " + tablePosition + " " + currentTurn.getBet() + " " + currentTurn.getStack());
		broadcastMessage("UPDATEPOTSIZE " + game.getPotSize());
		if (game.getPlayersAllInState()) {
			allInState();
		}
		currentTurnGC.deleteMessage();
	}

	private void raise() {
		if (validBetRaiseInput()) {
			int size = Integer.parseInt(incomingMessageWords[1]);
			System.out.println("RAISE " + size);
			game.raise(size);
			broadcastMessage("RAISE " + tablePosition + " " + size + " " + currentTurn.getStack());
			broadcastMessage("UPDATEPOTSIZE " + game.getPotSize());
		}
		currentTurnGC.deleteMessage();
	}

	private void bet() {
		if (validBetRaiseInput()) {
			int size = Integer.parseInt(incomingMessageWords[1]);
			game.bet(size);
			broadcastMessage("BET " + tablePosition + " " + size + " " + currentTurn.getStack());
			broadcastMessage("UPDATEPOTSIZE " + game.getPotSize());
		}
		currentTurnGC.deleteMessage();
	}

	private void allInState() {
		showPlayerCards();
		if (game.getGameState() == 0) {
			sleep(2000);
			dealFlop();
		}
		if (game.getGameState() == 1) {
			sleep(2000);
			dealTurn();
		}
		if (game.getGameState() == 2) {
			sleep(2000);
			dealRiver();
		}
		if (game.getGameState() == 3) {
			sleep(2000);
			showdown();
		}
	}

	private void setBestHandText() {
		for (Connection c : connections) {
			GameConnection gc = (GameConnection) c;
			Player p = gc.getPlayer();
			if (p.isStillInRound())
				gc.sendMessage("SETBESTHAND " + p.getHandValue().toString());
		}
	}

	private void startGame() {
		while (game.getRemainingPlayersInGame() > 1) {
			continueRound = true;
			startNewRound();
			while (continueRound) {
				if (game.agreementCheck()) {
					if (game.getGameState() == 0) {
						dealFlop();
						setBestHandText();
					} else if (game.getGameState() == 1) {
						dealTurn();
						setBestHandText();
					} else if (game.getGameState() == 2) {
						dealRiver();
						setBestHandText();
					} else {
						continueRound = false;
						showdown();
					}
				}
				if (continueRound) {
					currentTurn = game.getCurrentTurn();
					currentTurnGC = currentTurn.getGameConnection();
					tablePosition = currentTurn.getTablePosition();
					currentTurnGC.deleteMessage();
					displayButtons();
					boolean cont = true;
					while (cont) {
						if (currentTurnGC.getMessage() != null) {
							incomingMessageWords = currentTurnGC.getMessage().split(" ");
							String action = incomingMessageWords[0];
							if (action.equals("FOLD")) {
								fold();
								cont = false;
							} else if (action.equals("CHECK")) {
								check();
								cont = false;
							} else if (action.equals("CALL")) {
								call();
								cont = false;
							} else if (action.equals("RAISE")) {
								raise();
								cont = false;
							} else if (action.equals("BET")) {
								bet();
								cont = false;
							}
						} else
							for (int i = 0; i < 10; i++) {
								broadcastMessage("PROGRESSBAR " + tablePosition);
								sleep(150);
								if (i == 1 || i == 5)
									broadcastMessage("UNDECIDED " + tablePosition);
								if (currentTurnGC.getMessage() != null)
									i = 10;
							}
					}
				}
			}
		}
	}

	@Override
	public void run() {
		while (players.size() < numberOfPlayers) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				out = new DataOutputStream(socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				System.out.println(e + " failed to create server");
			}

			Player player = new Player(3000, "", players.size());
			GameConnection connection = new GameConnection(out, in, player, this);
			connections.add(connection);
			new Thread(connection).start();
			players.add(player);
			player.setGameConnection(connection);
			sleep(500);
		}
		game = new PokerGame(players);
		initializeInterface();
		startGame();
		isFull = true;
	}
}
