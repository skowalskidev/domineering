package domineering;

import java.util.HashSet;
import java.util.Set;

public class DomineeringBoard extends Board<DomineeringMove>{
		// Rename the players for this particular game:
		private static final Player playerV = Player.MAXIMIZER;
		private static final Player playerH = Player.MINIMIZER;
		
		/**
		 * Available maximizer moves
		 */
		private final HashSet<DomineeringMove> availablePlayerHMovesSet;
		private final HashSet<DomineeringMove> availablePlayerVMovesSet;
		
		//Board representation as bool 2d array true - taken, false - free space
		private boolean board[][];
		private int width;
		private int height;
		
		private boolean playerHTurn;
		
		/**
		 * Create a new, empty board
		 * @param width
		 * @param height
		 */
		public DomineeringBoard(boolean playerHStarts, int width, int height) {
			playerHTurn = playerHStarts;
			
			board = new boolean[width][height];
			this.width = width;
			this.height = height;
			
			availablePlayerHMovesSet = new HashSet<DomineeringMove>();
			availablePlayerVMovesSet = new HashSet<DomineeringMove>();
			
			setUpAvailableMoves();
		}	
		
		/**
		 * Create the board
		 * Used for creating new states i.e. moves
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
		private DomineeringBoard(boolean playerHTurn, HashSet<DomineeringMove> availablePlayerHMovesSet, HashSet<DomineeringMove> availablePlayerVMovesSet, boolean[][] board, int width, int height, DomineeringMove move) {
			this.availablePlayerHMovesSet = availablePlayerHMovesSet;
			this.availablePlayerVMovesSet = availablePlayerVMovesSet;
			
			this.board = board;
			this.width = width;
			this.height = height;
			
			this.playerHTurn = playerHTurn;
			
			//Playing a move
			if(playerHTurn){
				availablePlayerHMovesSet.remove(move);System.out.println("availablePlayerHMovesSet.isEmpty() "+availablePlayerHMovesSet.isEmpty());
				board[move.getX()][move.getY()] = true;//Add new move
				board[move.getX() + 1][move.getY()] = true;
			}
			else {
				availablePlayerVMovesSet.remove(move);System.out.println("availablePlayerVMovesSet.isEmpty() "+availablePlayerVMovesSet.isEmpty());
				board[move.getX()][move.getY()] = true;//Add new move
				board[move.getX()][move.getY() + 1] = true;
			}
			nextPlayer();
		}
		
		@Override
		Player nextPlayer() {
			return (playerHTurn = !playerHTurn) ?  playerH : playerV;
		}
		
		@Override
		Set<DomineeringMove> availableMoves() {
			System.out.println("availableMoves(), plauerHTurn " + playerHTurn);
			return playerHTurn ? availablePlayerHMovesSet : availablePlayerVMovesSet;
		}
		
		void setUpAvailableMoves() {
			HashSet<DomineeringMove> freeMoves = new HashSet<DomineeringMove>();
			for (int i = 0; i < width; i++){
				for (int j = 0; j < height; j++){
					if(board[i][j]){//Any move not possible at this position
						continue;
					}
					if(i + 1 < width && !board[i + 1][j]){//Add playerHMove on this position
						availablePlayerHMovesSet.add(new DomineeringMove(i, j));
					}
					if(j + 1 < height && !board[i][j + 1]){//Add playerVMove on this position
						availablePlayerVMovesSet.add(new DomineeringMove(i, j));
					}
				}
			}
		}
		
		@Override
		/**
		 * Returns the value of the game state heuristic for the current player
		 * @return
		 */
		int value() {
			assert(nextPlayer() == Player.MINIMIZER);//Only maximiser calls this?
			int heuristic;
			
			if(nextPlayer().equals(playerV)){
				heuristic = availablePlayerHMovesSet.size() - availablePlayerVMovesSet.size();
			}
			else {
				heuristic = availablePlayerVMovesSet.size() - availablePlayerHMovesSet.size();
			}
			
			if(heuristic > 0){//Current player has more available moves
				return 1;
			}
			else if(heuristic == 0){//Current player has the same no. of available moves
				return 0;
			}
			else {//Current player has less available moves
				return -1;
			}
		}
		
		@Override
		Board<DomineeringMove> play(DomineeringMove move) {
			return new DomineeringBoard(playerHTurn, availablePlayerHMovesSet, availablePlayerVMovesSet, board, width, height, move);
		}
		
		static private boolean disjoint(HashSet<DomineeringMove> a, HashSet<DomineeringMove> b) {
			return(intersection(a,b).isEmpty());
		}
		
		// The following short private methods are for readability. They
		// ensure immutability.
		
		// We promise we won't change the set a (so we clone it):
		static private HashSet<DomineeringMove> intersection(HashSet<DomineeringMove> a, HashSet<DomineeringMove> b) {
			@SuppressWarnings("unchecked")//Unchecked Cast to (HashSet<DomineeringMove>)
			HashSet<DomineeringMove> c = (HashSet<DomineeringMove>) a.clone(); // a.clone();
			c.retainAll(b);
			return c;
		}
}
