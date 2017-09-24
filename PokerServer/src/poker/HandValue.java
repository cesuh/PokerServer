package poker;

import java.util.*;

public class HandValue implements Comparable<HandValue> {

	private ArrayList<Card> bestCombination;
	private ArrayList<Card> playerAndBoardCards;
	private ArrayList<ArrayList<Card>> allCombinations;
	private int value;

	public HandValue(Hand hand, ArrayList<Card> common) {
		value = 1;
		allCombinations = new ArrayList<ArrayList<Card>>();
		bestCombination = new ArrayList<Card>();
		playerAndBoardCards = new ArrayList<Card>();
		playerAndBoardCards.add(hand.getLeft());
		playerAndBoardCards.add(hand.getRight());
		playerAndBoardCards.addAll(common);
		generateAllCombinations();
		findBestCombination();
	}

	public HandValue(ArrayList<Card> cards) {
		value = 1;
		allCombinations = new ArrayList<ArrayList<Card>>();
		bestCombination = new ArrayList<Card>();
		playerAndBoardCards = cards;
		generateAllCombinations();
		findBestCombination();
	}

	public int getValue() {
		return value;
	}

	/**
	 * returns A list of all possible five card combinations made of the hand and
	 * common cards combined (7 cards, 21 total possible combinations). Code from
	 * StackOverflow
	 */
	private void generateAllCombinations() {
		int k = 5;
		int[] s = new int[k];
		// first index sequence: 0, 1, 2, ...
		for (int i = 0; (s[i] = i) < k - 1; i++)
			;
		allCombinations.add(getSubset(playerAndBoardCards, s));
		for (;;) {
			int i;
			// find position of item that can be incremented
			for (i = k - 1; i >= 0 && s[i] == playerAndBoardCards.size() - k + i; i--)
				;
			if (i < 0) {
				break;
			} else {
				s[i]++; // increment this item
				for (++i; i < k; i++) { // fill up remaining items
					s[i] = s[i - 1] + 1;
				}
				allCombinations.add(getSubset(playerAndBoardCards, s));
			}
		}
	}

	/**
	 * Helper method for findAllCombinations (k-subset generation problem). Code
	 * from stackOverflow.
	 */
	private ArrayList<Card> getSubset(List<Card> input, int[] subset) {
		ArrayList<Card> result = new ArrayList<Card>();
		for (int i = 0; i < subset.length; i++)
			result.add(input.get(subset[i]));
		return result;
	}

	private void findBestCombination() {
		for (ArrayList<Card> list : allCombinations)
			findValue(list);
	}

	/**
	 * This method finds the best hand (5 cards) according to the rules of texas
	 * hold'em poker.
	 */
	private void findValue(ArrayList<Card> list) {
		if (checkForRoyalFlush(list)) {
			value = 10;
			bestCombination = list;
		} else if (value <= 9 && checkForStraightFlush(list)) {
			if (value < 9)
				bestCombination.addAll(list);
			else if (compareStraight(list) == 1)
				bestCombination = list;
			value = 9;
		} else if (checkForFourOfAKind(list)) {
			if (value < 8)
				bestCombination = list;
			else if (compareFourOfAKind(list) == 1)
				bestCombination = list;
			value = 8;
		} else if (value <= 7 && checkForFullHouse(list)) {
			if (value < 7)
				bestCombination = list;
			else if (compareHouse(list) == 1)
				bestCombination = list;
			value = 7;
		} else if (value <= 6 && checkForFlush(list)) {
			if (value < 6)
				bestCombination = list;
			else if (compareHighCard(list) == 1)
				bestCombination = list;
			value = 6;
		} else if (value <= 5 && checkForStraight(list)) {
			if (value < 5)
				bestCombination = list;
			else if (compareStraight(list) == 1)
				bestCombination = list;
			value = 5;
		} else if (value <= 4 && checkForThreeOfAKind(list)) {
			if (value < 4)
				bestCombination = list;
			else if (compareThreeOfAKind(list) == 1)
				bestCombination = list;
			value = 4;
		} else if (value <= 3 && checkForTwoPairs(list)) {
			if (value < 3)
				bestCombination = list;
			else if (compareTwoPairs(list) == 1)
				bestCombination = list;
			value = 3;
		} else if (value <= 2 && checkForOnePair(list)) {
			if (value < 2)
				bestCombination = list;
			else if (comparePair(list) == 1)
				bestCombination = list;
			value = 2;
		} else if (value == 1) {
			Collections.sort(list);
			if (bestCombination.isEmpty() || compareHighCard(list) == 1)
				bestCombination = list;
		}
	}

	// Checks if the combination is a royal flush
	private boolean checkForRoyalFlush(ArrayList<Card> list) {
		return (checkForFlush(list) && checkForStraight(list) && list.get(4).getRank() == 14
				&& list.get(0).getRank() == 10);
	}

	// checks if the combination is a straight flush
	private boolean checkForStraightFlush(ArrayList<Card> list) {
		return checkForFlush(list) && checkForStraight(list);
	}

	// also sorts the list so that the excess card comes last.
	private boolean checkForFourOfAKind(ArrayList<Card> list) {
		Collections.sort(list);
		// if the card that is not part of the 4-of-a-kind is sorted first, put it last.
		if (list.get(0).getRank() != list.get(1).getRank())
			list.add(list.remove(0));
		for (int i = 1; i <= 3; i++)
			if (list.get(i).getRank() != list.get(0).getRank())
				return false;
		return true;
	}

	// also sorts the hand so that the three of a kind is followed by the pair
	// for the purpose of comparing two full houses.
	private boolean checkForFullHouse(ArrayList<Card> list) {
		if (checkForThreeOfAKind(list)) {
			return list.get(3).getRank() == list.get(4).getRank();
		}
		return false;
	}

	private boolean checkForFlush(ArrayList<Card> list) {
		for (Card c : list)
			if (c.getSuit() != list.get(0).getSuit())
				return false;
		return true;
	}

	// Only need to sort the small straight, other straights are already sorted.
	private boolean checkForStraight(ArrayList<Card> list) {
		Collections.sort(list);
		boolean smallAce = (list.get(4).getRank() == 14 && list.get(0).getRank() == 2);
		if (smallAce) {
			for (int i = 0; i < 4; i++)
				if (list.get(i).getRank() != i + 2)
					return false;
			return true;
		} else
			for (int i = 0; i < 4; i++)
				if ((list.get(i).getRank() +1) != list.get(i + 1).getRank())
					return false;
		return true;
	}

	private boolean checkForThreeOfAKind(ArrayList<Card> list) {
		Collections.sort(list);
		// if the first two cards are not part of the three-of-a-kind, put them last.
		if(list.get(0).getRank() != list.get(1).getRank() || list.get(1).getRank() != list.get(2).getRank()) 
			list.add(list.remove(0));
		if(list.get(0).getRank() != list.get(1).getRank())
			list.add(list.remove(0));
		
		for (int i = 1; i <= 2; i++)
			if (list.get(i).getRank() != list.get(0).getRank())
				return false;
		return true;
	}

	private boolean checkForTwoPairs(ArrayList<Card> list) {
		Collections.sort(list);
		// If the first card is not part of a pair, put it last in the list.
		if (list.get(0).getRank() != list.get(1).getRank())
			list.add(list.remove(0));
		if(list.get(2).getRank() != list.get(3).getRank())
			list.add(list.remove(2));
		return list.get(0).getRank() == list.get(1).getRank() && list.get(1).getRank() != list.get(2).getRank()
				&& list.get(2).getRank() == list.get(3).getRank() && list.get(3).getRank() != list.get(4).getRank();
	}

	private boolean checkForOnePair(ArrayList<Card> list) {
		HashSet<Integer> set = new HashSet<Integer>();
		for (Card c : list)
			set.add(c.getRank());
		// If the size of the set is 4, then exactly 2 cards have the same rank
		return set.size() == 4;
	}

	private int compareHouse(ArrayList<Card> list) {
		int diff = list.get(0).getRank() - bestCombination.get(0).getRank();
		if (diff != 0)
			return diff;
		return list.get(4).getRank() - bestCombination.get(4).getRank();
	}

	private int compareFourOfAKind(ArrayList<Card> list) {
		int diff = list.get(0).getRank() - bestCombination.get(0).getRank();
		if (diff != 0)
			return diff;
		return list.get(4).getRank() - bestCombination.get(4).getRank();
	}

	private int compareStraight(ArrayList<Card> list) {
		return list.get(0).getRank() - bestCombination.get(0).getRank();
	}

	private int compareThreeOfAKind(ArrayList<Card> list) {
		int diff = list.get(0).getRank() - bestCombination.get(0).getRank();
		if (diff != 0)
			return diff;
		int diff2 = list.get(4).getRank() - bestCombination.get(4).getRank();
		if (diff2 != 0)
			return diff2;
		return list.get(3).getRank() - bestCombination.get(3).getRank();
	}

	private int compareTwoPairs(ArrayList<Card> list) {
		for (int i = 0; i < 5; i += 2)
			if (list.get(i).getRank() > bestCombination.get(i).getRank())
				return 1;
			else if (list.get(i).getRank() < bestCombination.get(i).getRank())
				return -1;
		return 0;
	}

	private int comparePair(ArrayList<Card> list) {
		int diff = list.get(0).getRank() - bestCombination.get(0).getRank();
		if (diff != 0)
			return diff;
		for (int i = 4; i > 1; i--)
			if (list.get(i).getRank() != bestCombination.get(i).getRank())
				return list.get(i).getRank() - bestCombination.get(i).getRank();
		return 0;
	}

	private int compareHighCard(ArrayList<Card> list) {
		for (int i = 4; i >= 0; i--)
			if (list.get(i).getRank() != bestCombination.get(i).getRank())
				return list.get(i).getRank() - bestCombination.get(i).getRank();
		return 0;
	}

	// At the end of a round, all remaining players' hands should be compared to
	// each other
	@Override
	public int compareTo(HandValue other) {
		if (value < other.value)
			return 1;
		if (value > other.value)
			return -1;
		if (value == 1)
			return compareHighCard(other.bestCombination);
		if (value == 2)
			return comparePair(other.bestCombination);
		if (value == 3)
			return compareTwoPairs(other.bestCombination);
		if (value == 4)
			return compareThreeOfAKind(other.bestCombination);
		if (value == 5)
			return compareStraight(other.bestCombination);
		// comparing two flushes is the same as comparing high card
		if (value == 6)
			return compareHighCard(other.bestCombination);
		if (value == 7)
			return compareHouse(other.bestCombination);
		if (value == 8)
			return compareFourOfAKind(other.bestCombination);
		// comparing two straight flushes is the same as comparing two straights
		if(value == 9)
			return compareStraight(other.bestCombination);
		return 0;
	}

	@Override
	public String toString() {
		if (value == 2)
			return "One pair";
		if (value == 3)
			return "Two pairs";
		if (value == 4)
			return "Three of a kind";
		if (value == 5)
			return "Straight";
		if (value == 6)
			return "Flush";
		if (value == 7)
			return "Full house";
		if (value == 8)
			return "Four of a kind";
		if (value == 9)
			return "Straight flush";
		if (value == 10)
			return "Royal flush";
		return "High card";

	}
}
