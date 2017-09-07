package poker;

import javafx.scene.Parent;
public class Card extends Parent implements Comparable<Card> {

	// number from 1-4
	private final int suit;
	// number from 2-14
	private final int rank;

	public Card(int suit, int rank) {
		this.suit = suit;
		this.rank = rank;
	}

	public int getSuit() {
		return this.suit;
	}

	public int getRank() {
		return this.rank;
	}

	@Override
	public int compareTo(Card other) {
		if (this.rank > other.rank)
			return 1;
		if (this.rank < other.rank)
			return -1;
		return 0;
	}

	@Override
	public String toString() {
		if (this.suit == 1)
			return "Spade" + rank;
		if (this.suit == 2)
			return "Heart" + rank;
		if (this.suit == 3)
			return "Diamond" + rank;
		return "Club" + rank;
	}
}
