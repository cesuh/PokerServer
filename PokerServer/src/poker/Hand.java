package poker;

public class Hand {


    private Card left;
    private Card right;

    public Hand(final Card left, final Card right) {
        this.left = left;
        this.right = right;
    }
    
    public final Card getLeft(){
    	return left;
    }
 
    public final Card getRight(){
    	return right;
    }
    
    public void setLeft(Card card){
    	left = card;
    }
    public void setRight(Card card){
    	right = card;
    }
    
    public void clearHand(){
    	left = right = null;
    }
    
}
