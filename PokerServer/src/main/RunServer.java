package main;

import serverNetwork.LobbyServer;

public class RunServer {

	public static void main(String[] args) {
		
		LobbyServer server = new LobbyServer(1100);
		new Thread(server).start();
		
	}
}
