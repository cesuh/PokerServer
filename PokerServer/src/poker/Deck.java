package poker;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

	private ArrayList<Card> deck;

	public Deck() {
		this.deck = new ArrayList<Card>();
		for (int i = 1; i < 5; i++)
			for (int j = 2; j < 15; j++)
				deck.add(new Card(i, j));
	}

	public ArrayList<Card> getDeckList() {
		return deck;
	}

	public Card dealCard() {
		return deck.remove(0);
	}

	public int size() {
		return deck.size();
	}

	public void shuffle() {
		Collections.shuffle(deck);
	}

}
