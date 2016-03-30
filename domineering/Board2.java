package domineering;

import java.util.LinkedHashMap;
import java.util.Set;

public abstract class Board2<Move> {

	/**
	 * The number of nodes I can explore before ceasing the current node expansion
	 * -1 : play optimally, > 0 use heuristic pruning
	 */
	int exploredLimit;
	/**
	 * Passed on to children nodes so this is most often a value from the parent
	 */
	int exploredCount;
	
	abstract Player nextPlayer();
	abstract Set<Move> availableMoves();
	abstract int value();
	abstract Board2<Move> play(Move move);
	abstract boolean gameOver();

	// Constructs the game tree of the Board2 using the minimax algorithm (without alpha-beta pruning):
	public GameTree2<Move> tree(int exploredCount) {
		if (availableMoves().isEmpty() || exploredCount == exploredLimit)
			return new GameTree2<Move>(this, new LinkedHashMap<Move, GameTree2<Move>>(), value());
		else
			this.exploredCount = ++exploredCount;
			return (nextPlayer() == Player.MAXIMIZER ? maxTree() : minTree());
	}

	// Two helper methods for that, which call the above method tree:
	public GameTree2<Move> maxTree() {//Player maximiser
		int optimalOutcome = Integer.MIN_VALUE;
		LinkedHashMap<Move, GameTree2<Move>> children = new LinkedHashMap<Move, GameTree2<Move>>();
		
		for (Move m : availableMoves()) {
			GameTree2<Move> subtree = play(m).tree(exploredCount);
			children.put(m, subtree);
			optimalOutcome = Math.max(optimalOutcome, subtree.optimalOutcome());
			if(playOptimally()){
				if(optimalOutcome == 1){
					children.clear();
					children.put(m, subtree);
					break;
				}
			}
			else if(optimalOutcome >= 2){
				children.clear();
				children.put(m, subtree);
				break;
			}	
		}

		return new GameTree2<Move>(this, children, optimalOutcome);
	}

	protected boolean playOptimally() {
		return exploredLimit == -1;
	}
	public GameTree2<Move> minTree() {//Player minimiser
		int optimalOutcome = Integer.MAX_VALUE;
		LinkedHashMap<Move, GameTree2<Move>> children = new LinkedHashMap<Move, GameTree2<Move>>();
		
		for (Move m : availableMoves()) {
			GameTree2<Move> subtree = play(m).tree(exploredCount);//Gets tree up to leaf with the outcome value of leaves from this tree
			children.put(m, subtree);
			optimalOutcome = Math.min(optimalOutcome, subtree.optimalOutcome());
		}

		return new GameTree2<Move>(this, children, optimalOutcome);//Return move plan
	}
}
