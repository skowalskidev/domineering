package domineering;

import java.util.LinkedHashMap;
import java.util.Set;

public abstract class Board2<Move> {
	abstract Player nextPlayer();
	abstract Set<Move> availableMoves();
	abstract int value();
	abstract Board2<Move> play(Move move);

	// Constructs the game tree of the Board2 using the minimax algorithm (without alpha-beta pruning):
	public GameTree2<Move> tree() {
		if (availableMoves().isEmpty())
			return new GameTree2<Move>(this, new LinkedHashMap<Move, GameTree2<Move>>(), value());
		else
			return (nextPlayer() == Player.MAXIMIZER ? maxTree() : minTree());
	}

	// Two helper methods for that, which call the above method tree:
	public GameTree2<Move> maxTree() {//Player maximiser
		assert (!availableMoves().isEmpty());

		/*
		 * TODO Calculate the tree after the first move to effectively cut down the number of comparisoons to 1/(width * height)
		 * */
		
		int optimalOutcome = Integer.MIN_VALUE;
		LinkedHashMap<Move, GameTree2<Move>> children = new LinkedHashMap<Move, GameTree2<Move>>();

		for (Move m : availableMoves()) {
			GameTree2<Move> subtree = play(m).tree();
			children.put(m, subtree);
			optimalOutcome = Math.max(optimalOutcome, subtree.optimalOutcome());
			if(optimalOutcome == 1)
				break;
		}

		return new GameTree2<Move>(this, children, optimalOutcome);
	}

	public GameTree2<Move> minTree() {//Player minimiser
		assert (!availableMoves().isEmpty());

		int optimalOutcome = Integer.MAX_VALUE;
		LinkedHashMap<Move, GameTree2<Move>> children = new LinkedHashMap<Move, GameTree2<Move>>();
		
		for (Move m : availableMoves()) {
			GameTree2<Move> subtree = play(m).tree();//Gets tree up to leaf with the outcome value of leaves from this tree
			children.put(m, subtree);
			optimalOutcome = Math.min(optimalOutcome, subtree.optimalOutcome());
		}

		return new GameTree2<Move>(this, children, optimalOutcome);
	}
}
