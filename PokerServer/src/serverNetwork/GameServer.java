package serverNetwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import poker.Card;
import poker.Hand;
import poker.Player;
import poker.PokerGame;

public class GameServer implements Runnable {

	private int numberOfPlayers;
	private int tablePosition;
	private final int portNumber;
	private boolean continueRound;
	private String gameName;
	private String password;
	private String[] incomingMessageWords;
	private Player currentTurn;
	private GameConnection currentTurnGC;
	private ArrayList<Player> players;
	private PokerGame game;
	private ServerSocket serverSocket;
	private DataOutputStream out;
	private DataInputStream in;

	public GameServer(String gameName, int numberOfPlayers, int portNumber) {
		this.portNumber = portNumber;
		this.numberOfPlayers = numberOfPlayers;
		this.gameName = gameName;
		players = new ArrayList<Player>();
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println("Failed to create gameServer socket");
		}
	}

	public ArrayList<Player> getGamePlayers() {
		return players;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public int getNumberOfgamePlayers() {
		return players.size();
	}

	public int getPortNumber() {
		return portNumber;
	}

	public String getPassword() {
		return password;
	}

	public String getGameName() {
		return gameName;
	}

	private void sendToAllPlayers(String message) {
		for (Player p : players)
			p.getGameConnection().sendMessage(message);
	}

	private void clearPlayerBets() {
		sendToAllPlayers("CLEARBETS");
	}

	private void initializeInterface() {
		for (Player p : players) {
			GameConnection c = p.getGameConnection();
			String[] words = null;
			if (c.getMessage() != null) {
				words = c.getMessage().split(" ");
				if (words[0].equals("NAME"))
					c.getPlayer().setName(words[1]);
				c.deleteMessage();
			}

			sendToAllPlayers("NAME " + p.getTablePosition() + " " + p.getName());
			sendToAllPlayers("PLAYERSTACK " + p.getTablePosition() + " " + game.getStartingStack());
			sendToAllPlayers("HIDELABEL");
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
		for (Player p : players) {
			GameConnection c = p.getGameConnection();
			Hand hand = p.getHand();
			Card left = hand.getLeft();
			Card right = hand.getRight();
			Player sb = game.getSmallBlind();
			Player bb = game.getBigBlind();
			c.sendMessage("DEALCARDS " + p.getTablePosition() + " " + left.getRank() + " " + left.getSuitNumber() + " "
					+ right.getRank() + " " + right.getSuitNumber() + " " + game.getRemainingPlayersInGame());
			c.sendMessage("DEALER " + game.getDealer().getTablePosition());
			c.sendMessage("BET " + sb.getTablePosition() + " " + sb.getBet() + " " + sb.getStack());
			c.sendMessage("BET " + bb.getTablePosition() + " " + bb.getBet() + " " + bb.getStack());
			c.sendMessage("POTSIZE " + game.getPotSize());
			c.sendMessage("CLEARBOARD");
			c.deleteMessage();
			sleep(400);
		}
	}

	private void dealFlop() {
		game.dealFlop();
		Card one = game.getBoard().get(0);
		Card two = game.getBoard().get(1);
		Card three = game.getBoard().get(2);
		sendToAllPlayers("DEALFLOP " + one.getRank() + " " + one.getSuitNumber() + " " + two.getRank() + " "
				+ two.getSuitNumber() + " " + three.getRank() + " " + three.getSuitNumber());
		clearPlayerBets();
	}

	private void dealTurn() {
		game.dealTurn();
		Card turn = game.getBoard().get(3);
		sendToAllPlayers("DEALTURN " + turn.getRank() + " " + turn.getSuitNumber());
		clearPlayerBets();
	}

	private void dealRiver() {
		game.dealRiver();
		Card river = game.getBoard().get(4);
		sendToAllPlayers("DEALRIVER " + river.getRank() + " " + river.getSuitNumber());
		clearPlayerBets();
	}

	private void showdown() {
		game.showdown();
		for (Player p : game.getRemainingPlayerList()) {
			Card left = p.getHand().getLeft();
			Card right = p.getHand().getRight();
			sendToAllPlayers("SHOWDOWN " + p.getTablePosition() + " " + left.getRank() + " " + left.getSuitNumber()
					+ " " + right.getRank() + " " + right.getSuitNumber() + " " + game.getRemainingPlayersInGame());
		}
		Player winner = game.getWinner();
		String tempName = winner.getName();
		int pos = winner.getTablePosition();
		sendToAllPlayers("NAME " + pos + " Winner");
		for (int i = 0; i < 10; i++) {
			sendToAllPlayers("CHANGEPLAYERBOXCOLOR " + pos);
			sleep(650);
		}
		sendToAllPlayers("NAME " + pos + " " + tempName);
		game.startNewRound();
	}

	/**
	 * Sends a message to the client of the current turn. The message has this
	 * format "format"
	 */
	private void displayButtons() {
		currentTurnGC.sendMessage("DISPLAYBUTTONS " + currentTurn.getBet() + " " + game.getCurrentBet() + " "
				+ game.getBigBlindValue() + " " + currentTurn.getStack());
		sendToAllPlayers("SHOWPROGRESSBAR " + tablePosition);
		currentTurnGC.deleteMessage();
	}

	private void fold() {
		sendToAllPlayers("FOLD " + tablePosition);
		currentTurnGC.deleteMessage();
		game.fold();
		if (game.getRemainingPlayersInRound() < 2) {
			sleep(300);
			game.startNewRound();
			continueRound = false;
		}
	}

	private void check() {
		sendToAllPlayers("CHECK " + tablePosition);
		game.check();
		currentTurnGC.deleteMessage();
	}

	private void call() {
		game.bet(game.getCurrentBet() - currentTurn.getBet());
		sendToAllPlayers("BET " + tablePosition + " " + currentTurn.getBet() + " " + currentTurn.getStack());
		sendToAllPlayers("UPDATEPOTSIZE " + game.getPotSize());
		currentTurnGC.deleteMessage();
	}

	private void bet() {
		incomingMessageWords = currentTurnGC.getMessage().split(" ");
		String msg = currentTurnGC.getMessage();
		if (incomingMessageWords[0].equals("BET")) {
			int betSize = -1;
			try {
				betSize = Integer.parseInt(incomingMessageWords[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Server bet index out of bounds. Message = " + msg);
			} catch (NumberFormatException e) {
				System.out.println("Server bet size unexpected type, expected int. Message = " + msg);
			}
			// Checks that the bet size is bigger than or equal to the current
			// bet + the big blind value (minimum bet allowed) and smaller or
			// equal to the player's stack (maximum bet allowed).
			if (betSize >= (game.getCurrentBet() + game.getBigBlindValue()) && betSize <= currentTurn.getStack()) {
				game.bet(betSize - currentTurn.getBet());
				sendToAllPlayers("BET " + tablePosition + " " + currentTurn.getBet() + " " + currentTurn.getStack());
				sendToAllPlayers("UPDATEPOTSIZE " + game.getPotSize());
				currentTurnGC.deleteMessage();
			}
		}
	}

	private void waitForDecision() throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			sendToAllPlayers("PROGRESSBAR " + tablePosition);
			Thread.sleep(150);
			if (i == 1 || i == 5)
				sendToAllPlayers("UNDECIDED " + tablePosition);
			if (currentTurnGC.getMessage() != null)
				break;
		}
	}

	private void setBestHandText() {
		for (Player p : game.getRemainingPlayersInRoundList()) {
			p.getGameConnection().sendMessage("SETBESTHAND " + p.getHandValue().toString());
		}
	}

	private void startGame() throws Exception {
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
					displayButtons();
					while (!currentTurn.getHasMadeDecision() || currentTurn.getBet() < game.getCurrentBet()) {
						if (currentTurnGC.getMessage() != null) {
							if (currentTurnGC.getMessage().equals("FOLD")) {
								fold();
								break;
							} else if (currentTurnGC.getMessage().equals("CHECK")) {
								check();
								break;
							} else if (currentTurnGC.getMessage().equals("CALL")) {
								call();
								break;
							} else {
								bet();
								break;
							}
						} else
							waitForDecision();
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
			} catch (IOException e) {
				System.out.println("Connection to server socket failed from server side");
			}
			try {
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Failed to create game server output stream");
			}
			try {
				in = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				System.out.println("Failed to create game server input stream");
			}
			GameConnection connection = new GameConnection(out, in, null, this);
			Player player = new Player(3000, "", players.size(), connection);
			connection.setPlayer(player);
			Thread thread = new Thread(connection);
			thread.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				System.out.println("Failed to sleep server thread");
			}
			players.add(player);
		}
		game = new PokerGame(players);
		initializeInterface();
		try {
			startGame();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
