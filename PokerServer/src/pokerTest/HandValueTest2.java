package pokerTest;

import java.util.ArrayList;
import java.util.List;
import poker.Card;
import poker.Deck;
import poker.HandValue;

public class HandValueTest2 {

	static int highCardCounter;
	static int pairCounter;
	static int twoPairCounter;
	static int threeOfAKindCounter;
	static int straightCounter;
	static int flushCounter;
	static int houseCounter;
	static int fourOfAKindCounter;
	static int straightFlushCounter;
	static int royalFlushCounter;
	static int totalCombinations;

	private static void testHV(HandValue hv) {
		int value = hv.getValue();
		if (value == 1)
			highCardCounter++;
		else if (value == 2)
			pairCounter++;
		else if (value == 3)
			twoPairCounter++;
		else if (value == 4)
			threeOfAKindCounter++;
		else if (value == 5)
			straightCounter++;
		else if (value == 6)
			flushCounter++;
		else if (value == 7)
			houseCounter++;
		else if (value == 8)
			fourOfAKindCounter++;
		else if (value == 9)
			straightFlushCounter++;
		else
			royalFlushCounter++;
	}

	private static ArrayList<Card> getSubset(List<Card> input, int[] subset) {
		ArrayList<Card> result = new ArrayList<Card>();
		for (int i = 0; i < subset.length; i++)
			result.add(input.get(subset[i]));
		return result;
	}

	public static void main(String[] args) {

		Deck deck = new Deck();
		ArrayList<Card> allCards = deck.getDeckList();

		int k = 7;
		int[] s = new int[k];
		// first index sequence: 0, 1, 2, ...
		for (int i = 0; (s[i] = i) < k - 1; i++)
			;

		HandValue hv = new HandValue((getSubset(allCards, s)));
		testHV(hv);
		for (;;) {
			int i;
			// find position of item that can be incremented
			for (i = k - 1; i >= 0 && s[i] == allCards.size() - k + i; i--)
				;
			if (i < 0) {
				break;
			} else {
				s[i]++; // increment this item
				for (++i; i < k; i++) { // fill up remaining items
					s[i] = s[i - 1] + 1;
				}
				HandValue hv2 = new HandValue((getSubset(allCards, s)));
				testHV(hv2);
			}
		}
		totalCombinations = highCardCounter + pairCounter + twoPairCounter + threeOfAKindCounter + straightCounter
				+ flushCounter + houseCounter + fourOfAKindCounter + straightFlushCounter + royalFlushCounter;

		System.out.println(
				"Exptected number of generated combinations = 52C7 = 133784560 // Actual number of generated combinations = "
						+ totalCombinations);
		System.out.println(
				"Expected number of high card combinations found as best hand = 23294460 // Actual number of high card cominations found as best hand = "
						+ highCardCounter);
		System.out.println(
				"Expected number of one pair combinations found as best hand = 58627800 // Actual number of one pair cominations found as best hand = "
						+ pairCounter);
		System.out.println(
				"Expected number of two pair combinations found as best hand = 31433400 // Actual number of two pair cominations found as best hand = "
						+ twoPairCounter);
		System.out.println(
				"Expected number of three of a kind combinations found as best hand = 6461620 // Actual number of three of a kind cominations found as best hand = "
						+ threeOfAKindCounter);
		System.out.println(
				"Expected number of straight combinations found as best hand = 6180020 // Actual number of straight cominations found as best hand = "
						+ straightCounter);
		System.out.println(
				"Expected number of flush combinations found as best hand = 4047644 // Actual number of flush cominations found as best hand = "
						+ flushCounter);
		System.out.println(
				"Expected number of house combinations found as best hand = 3473184 // Actual number of house cominations found as best hand = "
						+ houseCounter);
		System.out.println(
				"Expected number of four of a kind combinations found as best hand = 224848 // Actual number of four of a kind cominations found as best hand = "
						+ fourOfAKindCounter);
		System.out.println(
				"Expected number of straight flush combinations found as best hand = 37260 // Actual number of straight flush cominations found as best hand = "
						+ straightFlushCounter);
		System.out.println(
				"Expected number of royal flush combinations found as best hand = 4324	 // Actual number of royal flush cominations found as best hand = "
						+ royalFlushCounter);
	}
}
