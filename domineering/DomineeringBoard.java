package domineering;

import java.util.HashSet;
import java.util.Set;

public class DomineeringBoard extends Board<DomineeringMove> {

	private static Player playerV;
	private static Player playerH;

	/**
	 * Available moves
	 */
	private final HashSet<DomineeringMove> availablePlayerHMovesSet;
	private final HashSet<DomineeringMove> availablePlayerVMovesSet;

	// Board representation as bool 2d array true - taken, false - free space
	private boolean board[][];
	private int width;
	private int height;

	//private boolean playerHTurn;
	private boolean playerHStarts;
	
	/**
	 * Create a new, empty board
	 * 
	 * @param width
	 * @param height
	 */
	public DomineeringBoard(boolean playerHStarts, int width, int height) {
		//playerHTurn = playerHStarts; //The opposite so that when nextPlayer is called after the game starts the appropriate player will begin
		setPlayerStatuses(playerHStarts);
		
		board = new boolean[width][height];
		this.width = width;
		this.height = height;

		availablePlayerHMovesSet = new HashSet<DomineeringMove>();
		availablePlayerVMovesSet = new HashSet<DomineeringMove>();

		setUpAvailableMoves();
	}

	/**
	 * Create the board Used for creating new states i.e. moves
	 * 
	 * @param playerVMovesSet
	 * @param playerHMovesSet
	 * @param board
	 * @param width
	 * @param height
	 * @param moveX1
	 * @param moveY1
	 * @param moveX2
	 * @param moveY2
	 */
	private DomineeringBoard(boolean playerHStarts, HashSet<DomineeringMove> availablePlayerHMovesSet,HashSet<DomineeringMove> availablePlayerVMovesSet, boolean[][] board, int width, int height, DomineeringMove move) {
		setPlayerStatuses(playerHStarts);
		
		this.availablePlayerHMovesSet = availablePlayerHMovesSet;
		this.availablePlayerVMovesSet = availablePlayerVMovesSet;

		this.board = board;
		this.width = width;
		this.height = height;
		
		//this.playerHTurn = playerHTurn;

		// Playing a move
		//For each move played 4 of the opposite available moves are removed and 3 of the same type
		int moveX = move.getX();
		int moveY = move.getY();
		
		if (nextPlayer() == playerH) {
			board[moveX][moveY] = true;// Add new move
			board[moveX + 1][moveY] = true;
			
			availablePlayerHMovesSet.remove(new DomineeringMove(moveX - 1, moveY));//Remove move to the left
			availablePlayerHMovesSet.remove(new DomineeringMove(moveX + 1, moveY));//Remove move to the right
			
			availablePlayerVMovesSet.remove(new DomineeringMove(moveX + 1, moveY));//Remove potential available playerH move on the second dominoe half
			//Also remove the two potential playerV moves to the top of both newly occupied fields
			availablePlayerVMovesSet.remove(new DomineeringMove(moveX, moveY - 1));
			availablePlayerVMovesSet.remove(new DomineeringMove(moveX + 1, moveY - 1));
			
			assert(availablePlayerHMovesSet.contains(new DomineeringMove(moveX + 1, moveY)) == false);//availablePlayerHMovesSet contains taken second dominoe half?
			assert(availablePlayerVMovesSet.contains(new DomineeringMove(moveX + 1, moveY)) == false);//availablePlayerVMovesSet contains taken second dominoe half?
		} else {
			board[moveX][moveY] = true;// Add new move
			board[moveX][moveY + 1] = true;
			
			availablePlayerVMovesSet.remove(new DomineeringMove(moveX, moveY - 1));
			availablePlayerVMovesSet.remove(new DomineeringMove(moveX, moveY + 1));
			
			availablePlayerHMovesSet.remove(new DomineeringMove(moveX, moveY + 1));
			//Also remove the two potential playerH moves to the left of both newly occupied fields
			availablePlayerHMovesSet.remove(new DomineeringMove(moveX - 1, moveY));
			availablePlayerHMovesSet.remove(new DomineeringMove(moveX - 1, moveY + 1));
			
			assert(availablePlayerHMovesSet.contains(new DomineeringMove(moveX, moveY + 1)) == false);//availablePlayerHMovesSet contains taken second dominoe half? 
			assert(availablePlayerVMovesSet.contains(new DomineeringMove(moveX, moveY + 1)) == false);//availablePlayerVMovesSet contains taken second dominoe half? 
		}
		
		
		availablePlayerVMovesSet.remove(move);//Remove potential available move on the first dominoe half
		availablePlayerHMovesSet.remove(move);
		
		assert(availablePlayerHMovesSet.contains(move) == false);//availablePlayerHMovesSet contains taken first dominoe half?
		assert(availablePlayerVMovesSet.contains(move) == false);//availablePlayerVMovesSet contains taken first dominoe half?
	}
	
	private void setPlayerStatuses(boolean playerHStarts){
		this.playerHStarts = playerHStarts;
		if(playerHStarts){
			playerH = Player.MAXIMIZER;
			playerV = Player.MINIMIZER;
		}
		else{
			playerH = Player.MINIMIZER;
			playerV = Player.MAXIMIZER;
		}
	}
	
	@Override
	Player nextPlayer() {
		//return (playerHTurn = !playerHTurn) ? playerH : playerV;
		int noOfOccupiedSpaces = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if(board[i][j])
					noOfOccupiedSpaces++;
			}
		}

		if(noOfOccupiedSpaces % 4  == 0) {
			return playerHStarts ? playerH : playerV;//First player
		}
		else{
			return playerHStarts ? playerV : playerH;//Second player
		}
	}

	@Override
	Set<DomineeringMove> availableMoves() {
		return (nextPlayer() == playerH) ? availablePlayerHMovesSet : availablePlayerVMovesSet;
	}

	void setUpAvailableMoves() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				/*if (board[i][j]) {// Any move not possible at this position
					continue;
				}*/ //Not Required if board is always clean at the start
				if (i + 1 < width && !board[i + 1][j]) {// Add playerHMove on this position
					availablePlayerHMovesSet.add(new DomineeringMove(i, j));
				}
				if (j + 1 < height && !board[i][j + 1]) {// Add playerVMove on this position
					availablePlayerVMovesSet.add(new DomineeringMove(i, j));
				}
			}
		}
	}

	@Override
	/**
	 * Returns the value of the game state heuristic for the current player
	 * 
	 * @return
	 */
	int value() {// playerH(Maximizer) won(1) lost (-1)
		//assert (nextPlayer() == Player.MINIMIZER);// Only maximiser calls this?
		if(availablePlayerHMovesSet.size() > 0 && availablePlayerVMovesSet.size() > 0) {
			return 0;
		}
		else if(nextPlayer() == playerH){
			if(playerHStarts && availablePlayerHMovesSet.size() == 0){
				return -1;
			}
			return 1;
		}
		else{
			if(playerHStarts && availablePlayerVMovesSet.size() == 0){
				return 1;
			}
			return -1;
		}
		

		// TODO May be used for heuristic pruning
		/*
		 * int heuristic;
		 * 
		 * if(nextPlayer().equals(playerV)){ heuristic =
		 * availablePlayerHMovesSet.size() - availablePlayerVMovesSet.size(); }
		 * else { heuristic = availablePlayerVMovesSet.size() -
		 * availablePlayerHMovesSet.size(); }
		 * 
		 * if(heuristic > 0){//Current player has more available moves return 1;
		 * } else if(heuristic == 0){//Current player has the same no. of
		 * available moves return 0; } else {//Current player has less available
		 * moves return -1; }
		 */
	}

	@SuppressWarnings("unchecked")
	@Override
	Board<DomineeringMove> play(DomineeringMove move) {
		boolean newBoardRep[][] = deepCopyBoard();
		Board<DomineeringMove> tempBoard = new DomineeringBoard(playerHStarts, (HashSet<DomineeringMove>) availablePlayerHMovesSet.clone(), (HashSet<DomineeringMove>) availablePlayerVMovesSet.clone(), newBoardRep, width, height, move);
		
		assert(this.availableMoves().contains(move));//this contains move(not mutated) 
		assert(!tempBoard.availableMoves().contains(move));

		return tempBoard;
	}
	
	private boolean[][] deepCopyBoard() {
		boolean newBoardRep[][] = new boolean[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				newBoardRep[i][j] = board[i][j];
			}
		}
		return newBoardRep;
	}
	
	/*private HashSet<DomineeringMove> deepCopyHashSet(HashSet<DomineeringMove> original){
		HashSet<DomineeringMove> copy = new HashSet<DomineeringMove>(original.size());
	       
		Iterator<DomineeringMove> iterator = original.iterator();
		while(iterator.hasNext()){
		    //copy.add((DomineeringMove) iterator.next().clone());
		    DomineeringMove currentMove = iterator.next();
		    copy.add(new DomineeringMove(currentMove.getX(), currentMove.getY()));
		}
		
		return copy;
	}*/

	@Override
	public String toString(){
		String thisString = "";
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				thisString += board[x][y] ? "|-|" : x + "," + y;
				thisString += " ";
			}
			thisString += '\n';
		}
		return thisString;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////DEBUG//////////////////////////////////////////////////////////////////////////////////////////////

	static private boolean disjoint(HashSet<DomineeringMove> a, HashSet<DomineeringMove> b) {
		return (intersection(a, b).isEmpty());
	}

	// The following short private methods are for readability. They
	// ensure immutability.

	// We promise we won't change the set a (so we clone it):
	static private HashSet<DomineeringMove> intersection(HashSet<DomineeringMove> a, HashSet<DomineeringMove> b) {
		@SuppressWarnings("unchecked") // Unchecked Cast to
										// (HashSet<DomineeringMove>)
		HashSet<DomineeringMove> c = (HashSet<DomineeringMove>) a.clone(); // a.clone();
		c.retainAll(b);
		return c;
	}
}
