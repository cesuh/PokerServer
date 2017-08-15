package poker;

public class Hand {


    private Card left;
    private Card right;

    public Hand(Card left, Card right) {
        this.left = left;
        this.right = right;
    }
    
    public Card getLeft(){
    	return left;
    }
 
    public Card getRight(){
    	return right;
    }
    
    public void setLeft(Card card){
    	left = card;
    }
    public void setRight(Card card){
    	right = card;
    }
    
    public void clearHand(){
    	left = null;
    	right = null;
    }
    
}
