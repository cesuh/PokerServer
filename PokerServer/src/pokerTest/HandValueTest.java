package pokerTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import poker.Card;
import poker.Deck;
import poker.Hand;
import poker.HandValue;

public class HandValueTest {

	static double highCardCounter;
	static double pairCounter;
	static double twoPairCounter;
	static double threeOfAKindCounter;
	static double straightCounter;
	static double flushCounter;
	static double houseCounter;
	static double fourOfAKindCounter;
	static double straightFlushCounter = 0.000000;
	static double royalFlushCounter = 0.000000;

	private static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100000000; i++) {
			Deck deck = new Deck();
			deck.shuffle();
			Hand hand = new Hand(deck.dealCard(), deck.dealCard());
			ArrayList<Card> common = new ArrayList<Card>();
			for (int j = 0; j < 5; j++)
				common.add(deck.dealCard());
			HandValue hv = new HandValue(hand, common);
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
		System.out.println(
				"Statistics from 100 million hands using the HandValue algorithm. On the left the actual probability will be shown, and on the right the result of the algorithm. Close to equal numbers will give confidence that the algorithm is correct.");
		System.out.println(
				"Difference in numbers could indicate that the algorithm is wrong, that the RNG in the shuffle method is not sufficient or the sample size is not big enough. \n");
		System.out.println("Probability of High Card = 0.174 // Probability found by algorithm: "
				+ round((highCardCounter / 100000000), 3));
		System.out.println("Probability of One Pair = 0.438 // Probability found by algorithm: "
				+ round((pairCounter / 100000000), 3));
		System.out.println("Probability of Two Pairs = 0.235 // Probability found by algorithm: "
				+ round((twoPairCounter / 100000000), 3));
		System.out.println("Probability of Three of a Kind = 0.0483 // Probability found by algorithm: "
				+ round((threeOfAKindCounter / 100000000), 4));
		System.out.println("Probability of Straight = 0.0462 // Probability found by algorithm: "
				+ round((straightCounter / 100000000), 4));
		System.out.println("Probability of Flush = 0.0303 // Probability found by algorithm: "
				+ round((flushCounter / 100000000), 4));
		System.out.println("Probability of House = 0.026 // Probability found by algorithm: "
				+ round((houseCounter / 100000000), 3));
		System.out.println("Probability of Four of a Kind = 0.00168 // Probability found by algorithm: "
				+ round((fourOfAKindCounter / 100000000), 5));
		System.out.println("Probability of Straight Flush = 0.000279 // Probability found by algorithm: "
				+ round((straightFlushCounter / 100000000), 6));
		System.out.println("Probability of Royal Flush = 0.000032 // Probability found by algorithm: "
				+ round((royalFlushCounter / 100000000), 6));
	}
}
