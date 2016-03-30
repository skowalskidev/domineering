

import java.util.Map;

public class GameTree2<Move> {
	private final Board2<Move> board;//Last board in the tree
	private final Map<Move, GameTree2<Move>> children;
	private final int optimalOutcome;

	/**
	 * Constructor for optimal game
	 * @param board2
	 * @param children
	 * @param optimalOutcome
	 */
	public GameTree2(Board2<Move> board2, Map<Move, GameTree2<Move>> children, int optimalOutcome) {
		assert (board2 != null && children != null);
		this.board = board2;
		this.children = children;
		this.optimalOutcome = optimalOutcome;
	}

	public boolean isLeaf() {
		return (children.isEmpty());
	}

	// Getter methods:
	public Board2<Move> getBoard() {
		return board;
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
		c.comment(board + "\nThe optimal outcome is " + optimalOutcome);

		if (isLeaf()) {
			if(!board.gameOver()){//Leaf & !gameOver >> exploredLimit reached Do not explore further instead ask for the next move plan
				board.tree(0).firstPlayer(c);//this player was interrupted therefore let him continue
				return;
			}
			
			assert (optimalOutcome == board.value());
			c.end(board.value());
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
			optimalEntry.getValue().secondPlayer(c);//Board after playing the planned (stored) move
		}
	}
	
	// Plays second using this tree:
	public void secondPlayer(MoveChannel<Move> c) {
		c.comment(board + "\nThe optimal outcome is " + optimalOutcome);		
		
		if (isLeaf()) {
			if(!board.gameOver()){//Do not explore further instead ask for the next move plan
				/*
				 * TODO Do not ask for the next plan if its the console players turn and we can make a plan after his input
				 * If my heuristic is good enough then do not search further
				 */
				board.tree(0).secondPlayer(c);//this player was interrupted therefore let him continue
				return;
			}
			
			assert (optimalOutcome == board.value());
			c.end(board.value());
		} else {
			Move m = c.getMove();//Get move from player2(console)
			if (!children.containsKey(m))
				System.exit(1);
			//assert(children.containsKey(m));
			children.get(m).firstPlayer(c);//Board after playing the planned (stored) move
		}
	}
}
