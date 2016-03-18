package domineering;

import java.util.Map;

public class GameTree2<Move> {
	private final Board2<Move> Board2;
	private final Map<Move, GameTree2<Move>> children;
	private final int optimalOutcome;

	public GameTree2(Board2<Move> Board2, Map<Move, GameTree2<Move>> children, int optimalOutcome) {

		assert (Board2 != null && children != null);
		this.Board2 = Board2;
		this.children = children;
		this.optimalOutcome = optimalOutcome;
	}

	public boolean isLeaf() {
		return (children.isEmpty());
	}

	// Getter methods:
	public Board2<Move> Board2() {
		return Board2;
	}

	public Map<Move, GameTree2<Move>> children() {
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
		for (Map.Entry<Move, GameTree2<Move>> child : children.entrySet()) {
			size += child.getValue().size();
		}
		return size;
	}

	// We take the height of a leaf to be zero (rather than -1):
	public int height() {
		int height = -1;
		for (Map.Entry<Move, GameTree2<Move>> e : children.entrySet()) {
			height = Math.max(height, e.getValue().height());
		}
		return 1 + height;
	}

	// Plays first using this tree:
	public void firstPlayer(MoveChannel<Move> c) {
		c.comment(Board2 + "\nThe optimal outcome is " + optimalOutcome);

		if (isLeaf()) {
			assert (optimalOutcome == Board2.value());
			c.end(Board2.value());
		} else {
			Map.Entry<Move, GameTree2<Move>> optimalEntry = null;
			for (Map.Entry<Move, GameTree2<Move>> child : children.entrySet()) {
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
		c.comment(Board2 + "\nThe optimal outcome is " + optimalOutcome);

		if (isLeaf()) {
			assert (optimalOutcome == Board2.value());
			c.end(Board2.value());
		} else {
			Move m = c.getMove();//Get move from player2(console)
			if (!children.containsKey(m))
				System.exit(1);
			//assert(children.containsKey(m));
			children.get(m).firstPlayer(c);
		}
	}
}
