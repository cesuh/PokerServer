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

	public ArrayList<Card> getBestCombination() {
		return bestCombination;
	}

	/**
	 * returns A list of all possible five card combinations made of the hand
	 * and common cards combined (7 cards, 21 total possible combinations). This
	 * problem is also called k-subset generation and is a well-known problem in
	 * algorithms. Code from StackOverflow
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
	 * This method finds the best hand (5 cards) from the allCombinations list
	 * that gives the best result according to the rules of texas hold'em poker.
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
		return (checkForFlush(list) && checkForStraight(list) && list.get(4).getRank() == 14);
	}

	// checks if the combination is a straight flush
	private boolean checkForStraightFlush(ArrayList<Card> list) {
		return checkForFlush(list) && checkForStraight(list);
	}

	// also sorts the list so that the excess card comes last.
	private boolean checkForFourOfAKind(ArrayList<Card> list) {
		Collections.sort(list);
		int c1 = 1;
		int c2 = 1;
		for (int i = 0; i < 5; i++)
			if (i != 0 && list.get(i).getRank() == list.get(0).getRank())
				c1++;
			else if (i != 1 && list.get(i).getRank() == list.get(1).getRank())
				c2++;
		if (c1 == 4 || c2 == 4) {
			if (list.get(0).getRank() != list.get(1).getRank())
				list.add(list.remove(0));
			return true;
		}
		return false;
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
		Collections.sort(list);
		for (Card c : list)
			if (c.getSuit() != list.get(0).getSuit())
				return false;
		return true;
	}

	// Only need to sort the small straight, other straights are already sorted.
	private boolean checkForStraight(ArrayList<Card> list) {
		Collections.sort(list);
		// Check for a straight where the Ace is a 1
		boolean checkForSmallAce = (list.get(4).getRank() == 14 && list.get(0).getRank() == 2);
		if (checkForSmallAce) {
			for (int i = 1; i < 4; i++)
				if (list.get(i).getRank() != i + 2)
					return false;
			// we have a 1-5 straight and should sort it to get the Ace first
			ArrayList<Card> copy = new ArrayList<Card>();
			copy.add(list.remove(4));
			copy.addAll(list);
			list.clear();
			list.addAll(copy);
			return true;
		}
		int first = list.get(0).getRank();
		for (int i = 1; i < 5; i++)
			if (list.get(i).getRank() != (first + i))
				return false;
		return true;
	}

	private boolean checkForThreeOfAKind(ArrayList<Card> list) {
		Collections.sort(list);
		int[] c = { 1, 1, 1 };
		// first we need to find out which of the first three
		// cards there are three of. It could be 1, 2 or all 3.
		for (int i = 0; i < 5; i++)
			if (i != 0 && list.get(i).getRank() == list.get(0).getRank()) {
				c[0]++;
			} else if (i != 1 && list.get(i).getRank() == list.get(1).getRank()) {
				c[1]++;
			} else if (i != 2 && list.get(i).getRank() == list.get(2).getRank()) {
				c[2]++;
			}
		// Then we need to extract these 3 and the 2 others
		// into separate list so that we can sort the final list.
		if (c[0] == 3 || c[1] == 3 || c[2] == 3) {
			ArrayList<Card> l1 = new ArrayList<Card>();
			ArrayList<Card> l2 = new ArrayList<Card>();
			for (int i = 0; i < 3; i++)
				if (c[i] == 3) {
					for (Card c2 : list)
						if (c2.getRank() == list.get(i).getRank())
							l1.add(c2);
						else
							l2.add(c2);
				}
			list.clear();
			list.addAll(l1);
			Collections.sort(l2);
			list.addAll(l2);
			return true;
		}
		return false;
	}

	private boolean checkForTwoPairs(ArrayList<Card> list) {
		Collections.sort(list);
		int found = -1;
		ArrayList<Card> l1 = new ArrayList<Card>();
		ArrayList<Card> l2 = new ArrayList<Card>();
		for (int i = 1; i < 5; i++)
			if (list.get(i).getRank() == list.get(i - 1).getRank()) {
				found = i;
				l1.add(list.get(i));
				l1.add(list.get(i - 1));
				break;
			}
		if (found < 3 && found > 0)
			for (int i = found + 2; i < 5; i++)
				if (list.get(i).getRank() == list.get(i - 1).getRank()) {
					l2.add(list.get(i));
					l2.add(list.get(i - 1));
					Card excess = null;
					for (Card c : list)
						if (c.getRank() != l1.get(0).getRank() && c.getRank() != l2.get(0).getRank())
							excess = c;
					list.clear();
					if (l2.get(0).getRank() > l1.get(0).getRank()) {
						list.addAll(l2);
						list.addAll(l1);
					} else {
						list.addAll(l1);
						list.addAll(l2);
					}
					list.add(excess);
					return true;
				}
		return false;
	}

	private boolean checkForOnePair(ArrayList<Card> list) {
		Collections.sort(list);
		ArrayList<Card> l1 = new ArrayList<Card>();
		ArrayList<Card> l2 = new ArrayList<Card>();
		boolean found = false;
		for (int i = 1; i < 5; i++)
			if (list.get(i).getRank() == list.get(i - 1).getRank()) {
				l1.add(list.get(i));
				l1.add(list.get(i - 1));
				found = true;
				break;
			}
		if (found) {
			for (Card c : list)
				if (c.getRank() != l1.get(0).getRank())
					l2.add(c);
			Collections.sort(l2);
			list.clear();
			list.addAll(l1);
			list.addAll(l2);
			return true;
		}
		return false;
	}

	private int compareHouse(ArrayList<Card> list) {
		for (int i = 0; i < 5; i += 4)
			if (list.get(i).getRank() > bestCombination.get(i).getRank())
				return 1;
			else if (list.get(i).getRank() < bestCombination.get(i).getRank())
				return -1;
		return 0;
	}

	private int compareFourOfAKind(ArrayList<Card> list) {
		if (list.get(0).getRank() > bestCombination.get(0).getRank())
			return 1;
		else if (list.get(0).getRank() < bestCombination.get(0).getRank())
			return -1;
		return 0;
	}

	private int compareStraight(ArrayList<Card> list) {
		if (list.get(4).getRank() > bestCombination.get(4).getRank())
			return 1;
		else if (list.get(4).getRank() < bestCombination.get(4).getRank())
			return -1;
		return 0;
	}

	private int compareThreeOfAKind(ArrayList<Card> list) {
		if (list.get(0).getRank() > bestCombination.get(0).getRank())
			return 1;
		else if (list.get(0).getRank() < bestCombination.get(0).getRank())
			return -1;
		else
			for (int i = 4; i > 2; i--)
				if (list.get(i).getRank() > bestCombination.get(i).getRank())
					return 1;
				else if (list.get(i).getRank() < bestCombination.get(i).getRank())
					return -1;
		return 0;
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
		if (list.get(0).getRank() > bestCombination.get(0).getRank())
			return 1;
		else if (list.get(0).getRank() < bestCombination.get(0).getRank())
			return -1;
		else
			for (int i = 4; i > 1; i--)
				if (list.get(i).getRank() > bestCombination.get(i).getRank())
					return 1;
				else if (list.get(i).getRank() < bestCombination.get(i).getRank())
					return -1;
		return 0;
	}

	private int compareHighCard(ArrayList<Card> list) {
		for (int i = 4; i >= 0; i--)
			if (list.get(i).getRank() > bestCombination.get(i).getRank())
				return 1;
			else if (list.get(i).getRank() < bestCombination.get(i).getRank())
				return -1;
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
			return compareHighCard(other.getBestCombination());
		if (value == 2)
			return comparePair(other.getBestCombination());
		if (value == 3)
			return compareTwoPairs(other.getBestCombination());
		if (value == 4)
			return compareThreeOfAKind(other.getBestCombination());
		if (value == 5)
			return compareStraight(other.getBestCombination());
		// comparing two flushes is the same as comparing high card
		if (value == 6)
			return compareHighCard(other.getBestCombination());
		if (value == 7)
			return compareHouse(other.getBestCombination());
		if (value == 8)
			return compareFourOfAKind(other.getBestCombination());
		// comparing two straight flushes is the same as comparing two straights
		if (value == 9)
			return compareStraight(other.getBestCombination());
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
