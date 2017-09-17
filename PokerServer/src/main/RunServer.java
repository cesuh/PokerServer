package main;

import java.io.IOException;

import serverNetwork.LobbyServer;

public class RunServer {

	public static void main(String[] args) throws IOException {
		LobbyServer server = new LobbyServer();
		new Thread(server).start();
	}
}
