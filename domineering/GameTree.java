

import java.util.*;

public class GameTree<Move> {
	private final Board<Move> board;
	private final Map<Move, GameTree<Move>> children;
	private final int optimalOutcome;

	public GameTree(Board<Move> board, Map<Move, GameTree<Move>> children, int optimalOutcome) {

		assert (board != null && children != null);
		this.board = board;
		this.children = children;
		this.optimalOutcome = optimalOutcome;
	}

	public boolean isLeaf() {
		return (children.isEmpty());
	}

	// Getter methods:
	public Board<Move> board() {
		return board;
	}

	public Map<Move, GameTree<Move>> children() {
		return children;
	}

	public int optimalOutcome() {
		return optimalOutcome;
	}

	// The following two methods are for game tree statistics only.
	// They are not used for playing.

	// Number of tree nodes:
	public int size() {
		int size = 1;
		for (Map.Entry<Move, GameTree<Move>> child : children.entrySet()) {
			size += child.getValue().size();
		}
		return size;
	}

	// We take the height of a leaf to be zero (rather than -1):
	public int height() {
		int height = -1;
		for (Map.Entry<Move, GameTree<Move>> e : children.entrySet()) {
			height = Math.max(height, e.getValue().height());
		}
		return 1 + height;
	}

	// Plays first using this tree:
	public void firstPlayer(MoveChannel<Move> c) {
		c.comment(board + "\nThe optimal outcome is " + optimalOutcome);

		if (isLeaf()) {
			assert (optimalOutcome == board.value());
			c.end(board.value());
		} else {
			Map.Entry<Move, GameTree<Move>> optimalEntry = null;
			for (Map.Entry<Move, GameTree<Move>> child : children.entrySet()) {
				if (optimalOutcome == child.getValue().optimalOutcome) {
					optimalEntry = child;
					break;
				}
			}
			assert (optimalEntry != null);
			c.giveMove(optimalEntry.getKey());
			optimalEntry.getValue().secondPlayer(c);
		}
	}

	// Plays second using this tree:
	public void secondPlayer(MoveChannel<Move> c) {
		c.comment(board + "\nThe optimal outcome is " + optimalOutcome);

		if (isLeaf()) {
			assert (optimalOutcome == board.value());
			c.end(board.value());
		} else {
			Move m = c.getMove();//Get move from player2(console)
			if (!children.containsKey(m))
				System.exit(1);
			//assert(children.containsKey(m));
			children.get(m).firstPlayer(c);
		}
	}
}
