package domineering;

import java.util.HashSet;
import java.util.Set;

public class DomineeringBoard extends Board2<DomineeringMove> {

	private static Player playerV;
	private static Player playerH;

	/**
	 * Available moves
	 * Keeps track of nodes in which playerH can move
	 * NOTE: Size of this set does not state the number of actual available moves!
	 */
	private final HashSet<DomineeringMove> availablePlayerHMovesSet;
	private final HashSet<DomineeringMove> availablePlayerVMovesSet;
	
	/////////////////////////////////////////////////////////////////////////////////////////Heuristic pruning/////////////////////////////////////////////////////////////////////////////////////////	
	private int noOfAvailableHMoves;
	private int noOfAvailableVMoves;
	
	// Board2 representation as bool 2d array true - taken, false - free space
	private boolean Board2[][];
	private int width;
	private int height;

	//private boolean playerHTurn;
	private boolean maximiserStarts;
	private boolean playerHMaximiser;
	
	/**
	 * Create a new, empty Board2
	 * @param playOptimally
	 * @param exploredLimit
	 * @param maximiserStarts
	 * @param playerHMaximiser
	 * @param width
	 * @param height
	 */
	public DomineeringBoard(int exploredLimit, boolean maximiserStarts, boolean playerHMaximiser, int width, int height) {
		this.exploredLimit = exploredLimit;
		exploredCount = 0;//At the beginning (creating first game board)
		
		setPlayerStatuses(maximiserStarts, playerHMaximiser);
		
		Board2 = new boolean[width][height];
		this.width = width;
		this.height = height;

		availablePlayerHMovesSet = new HashSet<DomineeringMove>();
		availablePlayerVMovesSet = new HashSet<DomineeringMove>();
		
		setUpAvailableMoves();
	}

	/**
	 * Create the Board2 Used for creating new states i.e. moves
	 * @param exploredLimit
	 * @param exploredCount
	 * @param maximiserStarts
	 * @param playerHMaximiser
	 * @param availablePlayerHMovesSet
	 * @param availablePlayerVMovesSet
	 * @param Board2
	 * @param width
	 * @param height
	 * @param move
	 */
	private DomineeringBoard(int exploredLimit, int exploredCount, boolean maximiserStarts, boolean playerHMaximiser, 
			HashSet<DomineeringMove> availablePlayerHMovesSet,HashSet<DomineeringMove> availablePlayerVMovesSet, 
			boolean[][] Board2, int width, int height, DomineeringMove move) {
		
		this.exploredLimit = exploredLimit;
		this.exploredCount = exploredCount;

		
		setPlayerStatuses(maximiserStarts, playerHMaximiser);
		
		this.availablePlayerHMovesSet = availablePlayerHMovesSet;
		this.availablePlayerVMovesSet = availablePlayerVMovesSet;

		this.Board2 = Board2;
		this.width = width;
		this.height = height;

		// Playing a move
		//For each move played 4 of the opposite available moves are removed and 3 of the same type
		int moveX = move.getX();
		int moveY = move.getY();
		
		//Also used further down
		Player nextPlayer = nextPlayer();
		
		if (nextPlayer == playerH) {
			Board2[moveX][moveY] = true;// Add new move
			Board2[moveX + 1][moveY] = true;
			
			availablePlayerHMovesSet.remove(new DomineeringMove(moveX - 1, moveY));//Remove move to the left
			availablePlayerHMovesSet.remove(new DomineeringMove(moveX + 1, moveY));//Remove move to the right
			
			availablePlayerVMovesSet.remove(new DomineeringMove(moveX + 1, moveY));//Remove potential available playerH move on the second dominoe half
			//Also remove the two potential playerV moves to the top of both newly occupied fields
			availablePlayerVMovesSet.remove(new DomineeringMove(moveX, moveY - 1));
			availablePlayerVMovesSet.remove(new DomineeringMove(moveX + 1, moveY - 1));
			
			assert(availablePlayerHMovesSet.contains(new DomineeringMove(moveX + 1, moveY)) == false);//availablePlayerHMovesSet contains taken second dominoe half?
			assert(availablePlayerVMovesSet.contains(new DomineeringMove(moveX + 1, moveY)) == false);//availablePlayerVMovesSet contains taken second dominoe half?
		} else {
			Board2[moveX][moveY] = true;// Add new move
			Board2[moveX][moveY + 1] = true;
			
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
	
		/////////////////////////////////////////////////////////////////////////////////////////Updating number of possible moves for both players/////////////////////////////////////////////////////////////////////////////////////////
		//NAIVE COUNTING METHOD - GURANTEED TO WORK
		if(!playOptimally())//Have to use heuristics
			countAvailableMoves();
	}
	
	private void countAvailableMoves() {
		noOfAvailableHMoves = 0;
		noOfAvailableVMoves = 0;
		//Have to count separately H & V moves
		//H moves
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width - 1; x++){
				if (!Board2[x][y] && !Board2[x + 1][y]){
					noOfAvailableHMoves++;
					x++;//Results in skipping counted move
					continue;
				}
			}
		}
		
		//V moves
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height - 1; y++){
				if (!Board2[x][y] && !Board2[x][y + 1]){
					noOfAvailableVMoves++;
					y++;//Results in skipping counted move
					continue;
				}
			}
		}
	}

	private void setPlayerStatuses(boolean maximiserStarts, boolean playerHMaximiser){
		this.maximiserStarts = maximiserStarts;
		this.playerHMaximiser = playerHMaximiser;
		
		if(playerHMaximiser){
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
				if(Board2[i][j])
					noOfOccupiedSpaces++;
			}
		}

		if(noOfOccupiedSpaces % 4  == 0) {//Maximiser does not always start first
			if(playerHMaximiser){
				if(maximiserStarts){
					return playerH;
				}
				else {
					return playerV;
				}
			}
			else {
				if(maximiserStarts){
					return playerV;
				}
				else {
					return playerH;
				}
			}
		}
		else{
			if(playerHMaximiser){
				if(maximiserStarts){
					return playerV;
				}
				else {
					return playerH;
				}
			}
			else {
				if(maximiserStarts){
					return playerH;
				}
				else {
					return playerV;
				}
			}
		}
	}

	@Override
	Set<DomineeringMove> availableMoves() {
		return (nextPlayer() == playerH) ? availablePlayerHMovesSet : availablePlayerVMovesSet;
	}

	void setUpAvailableMoves() {
		noOfAvailableHMoves = Math.floorDiv(width, 2) * height;
		noOfAvailableVMoves = Math.floorDiv(height, 2) * width;
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				/*if (Board2[i][j]) {// Any move not possible at this position
					continue;
				}*/ //Not Required if Board2 is always clean at the start
				if (i + 1 < width && !Board2[i + 1][j]) {// Add playerHMove on this position
					availablePlayerHMovesSet.add(new DomineeringMove(i, j));
				}
				if (j + 1 < height && !Board2[i][j + 1]) {// Add playerVMove on this position
					availablePlayerVMovesSet.add(new DomineeringMove(i, j));
				}
			}
		}
	}
	
	/**
	 * Used in conjuction with value when using heuristics (playing non-optimally)
	 * @return has the game ended (either player lost)
	 */
	@Override
	public boolean gameOver(){
		return availablePlayerHMovesSet.size() > 0 && availablePlayerVMovesSet.size() > 0 ? false : true;
	}
	
	@Override
	/**
	 * Returns the value of the game state heuristic for the current player
	 * NOTE: Only called at the leaf node / exploredLimit
	 * @return
	 */
	int value() {// Maximizer won(1) lost (-1)
		if(availablePlayerHMovesSet.size() > 0 && availablePlayerVMovesSet.size() > 0) {
			if(playOptimally())
				return 0;
			
			if(playerHMaximiser)
				return noOfAvailableHMoves - noOfAvailableVMoves;
			return noOfAvailableVMoves - noOfAvailableHMoves;
		}
		else if(nextPlayer() == playerH){
			if(playerHMaximiser && availablePlayerHMovesSet.size() == 0){
				return -1;
			}
			return 1;
		}
		else{
			if(playerHMaximiser && availablePlayerVMovesSet.size() == 0){
				return 1;
			}
			return -1;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	Board2<DomineeringMove> play(DomineeringMove move) {
		boolean newBoard2Rep[][] = deepCopyBoard2();
		Board2<DomineeringMove> tempBoard2 = new DomineeringBoard(exploredLimit, exploredCount, maximiserStarts, playerHMaximiser, (HashSet<DomineeringMove>) availablePlayerHMovesSet.clone(), (HashSet<DomineeringMove>) availablePlayerVMovesSet.clone(), newBoard2Rep, width, height, move);
		
		assert(this.availableMoves().contains(move));//this contains move(not mutated) 
		assert(!tempBoard2.availableMoves().contains(move));

		return tempBoard2;
	}
	
	private boolean[][] deepCopyBoard2() {
		boolean newBoard2Rep[][] = new boolean[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				newBoard2Rep[i][j] = Board2[i][j];
			}
		}
		return newBoard2Rep;
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
				thisString += Board2[x][y] ? "|-|" : x + "," + y;
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//THE FOLOWING CHECKS MAY FAIL AND ARE LESS EFFICIENT!, TURN TO THE NAIVE COUNTING METHOD(THIS CAN TAKE MORE STEPS ON LARGE MAPS)
			//Types of cut offs (only one version shown e.g. can be rotated)
			// + 	: occupied node
			// * 	: free node
			// ___ 	: placed move (horizontal in the examples) that provides cut offs
			
			//KISSINGPIRATES - check for first as they are formed by 2 PIRATE cut offs
			// + *
			// ___
			//
			// * +
			
			//PIRATE
			// + *
			// ___
			
			//LONGFACE
			// * *
			// ___
			/*
			if (nextPlayer == playerH) {
				//First check if player cut off himself
				if(moveX > 0 && moveX < width - 2 && !Board2[moveX - 1][moveY] && !Board2[moveX + 2][moveY]){
					noOfAvailableHMoves -= 2;//Player cut off himself
				}
				else {
					noOfAvailableHMoves--;
				}
				
				//Now check if the player cut off opponent's moves
				//First check special case - special case allows to make a move without cutting off anything
				/*if(height % 2 != 0 && (moveY == 0 || moveY == height - 1)){//odd height
					if(moveY == 0){//Count number of possible moves after move and deduce the cut off quantity
						if (!Board2[moveX][moveY + 1] && !Board2[moveX + 1][moveY + 1])//if not both nodes occupied under new board(move)
						{
							int y = 2;
							int x = 0;
							int stopExploringAtCullumn = 2;
							if(Board2[moveX][moveY + 1]){//Only left node occupied under new board(move) therefore did not cut off any moves on the left
								x = 1;
							}
							else if(Board2[moveX + 1][moveY + 1]){//Only right node occupied under new board(move) therefore did not cut off any moves on the right
								stopExploringAtCullumn = 1;
							}
							/*else{//Both nodes under are free
								
							}*/
							
							/*for (; x < stopExploringAtCullumn; x++){
								for (; y < height - 1; y++){
									
								}
							}
						}
					}
					else {
						
					}
					
				}
				//Checking for KISSINGPIRATES & LONGFACE - provide 2 cut offs
				else*/ /*if (moveX < width - 1 && moveY > 0 && moveY < height - 1 && !Board2[moveX][moveY - 1] && Board2[moveX + 1][moveY - 1] && Board2[moveX][moveY + 1] && !Board2[moveX + 1][moveY + 1]
						|| moveY > 0 && moveX < width - 1 && moveY < height - 1 && Board2[moveX][moveY - 1] && !Board2[moveX + 1][moveY - 1] && !Board2[moveX][moveY + 1] && Board2[moveX + 1][moveY + 1]
						//LONGFACE
						|| moveY > 0 && moveX < width - 1 && !Board2[moveX][moveY - 1] && !Board2[moveX + 1][moveY - 1]
						|| moveX < width - 1 && moveY < height - 1 && !Board2[moveX][moveY + 1] && !Board2[moveX + 1][moveY + 1]){
					noOfAvailableVMoves -= 2;
				}
				//PIRATE provides 1 cut off, no other scenarios possible
				else{
					noOfAvailableVMoves--;
				}
				
			}else{
				//First check if player cut off himself
				if(moveY > 0 && moveY < height - 2 && !Board2[moveX][moveY - 1] && !Board2[moveX][moveY + 2]){
					noOfAvailableVMoves -= 2;//Player cut off himself
				}
				else {
					noOfAvailableVMoves--;
				}
				
				if (moveX > 0 && moveY < height - 1 && moveX < width - 1 && Board2[moveX - 1][moveY] && !Board2[moveX - 1][moveY + 1] && !Board2[moveX + 1][moveY] && Board2[moveX + 1][moveY + 1]
						|| moveX > 0 && moveY < height - 1 && moveX < width - 1 && !Board2[moveX - 1][moveY] && Board2[moveX - 1][moveY + 1] && Board2[moveX + 1][moveY] && !Board2[moveX + 1][moveY + 1]
						//LONGFACE
						|| moveX > 0 && moveY < height - 1 && !Board2[moveX - 1][moveY] && !Board2[moveX - 1][moveY + 1]
						|| moveX < width - 1 && moveY < height - 1 && !Board2[moveX + 1][moveY] && !Board2[moveX + 1][moveY + 1]){
					noOfAvailableVMoves -= 2;
				}
				//PIRATE provides 1 cut off, no other scenarios possible
				else{
					noOfAvailableVMoves--;
				}
				 
			}*/
}
