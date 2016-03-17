package domineering;

/**
 * Represents moves on the board as int positions e.g. top left move = 0,0
 * 
 * If the pos of a playerHMove is stored then x & y represent the location of the left half of the horizontal dominoe
 * If the pos of a playerVMove is stored then x & y represent the location of the top half of the vertical dominoe
 * 
 * The DomineeringMove instances are stored in separate sets which segregate playerHMoves & playerVMoves
 * @author Szymon
 */

public class DomineeringMove {
	private final int x;
	private final int y;
	//TODO could add x2 & y2 yet it may be better to use cleverness to avoid excessive data

	/**	
	 * 
	 * @param x move x pos
	 * @param y move y pos
	 */

	public DomineeringMove(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof DomineeringMove)){
			return false;
		}
		else{
			return ((DomineeringMove) o).getX() == x && ((DomineeringMove) o).getY() == y;
		}
	}
	
	@Override
	public int hashCode(){
		return x+2*y;
	}
	
	@Override
	public String toString(){
		return Integer.toString(x) + ", " + Integer.toBinaryString(y);
	}
}
