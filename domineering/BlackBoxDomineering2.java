package domineering;

/**
 * TODO Change to proper BBD2 temporarily this is used for testing
 * @author Szymon
 *
 */
public class BlackBoxDomineering2 {
	/**
	 * -1 when playing optimally > 0 when using heuristic pruning
	 */
	private final static int exploredLimit = 3;
	
	public static class CommandLineDom implements MoveChannel<DomineeringMove> {
		@Override
		public DomineeringMove getMove() {
			String input = System.console().readLine();//"Enter your move: "
			assert (input.length() == 3);//Input format: x y
			return new DomineeringMove(Character.getNumericValue(input.charAt(0)), Character.getNumericValue(input.charAt(2)));
		}

		public void giveMove(DomineeringMove move) {
			System.out.println(move);
		}

		public void comment(String msg) {
			//System.out.println(msg);
		}

		public void end(int value) {
			//System.out.println("Game over. The result is " + value);
		}
	}
	
	/**
	 * Wait for player to move & calculate the tree after the first move to effectively cut down the number of comparisoons to 1/(width * height) of original
	 * @param board
	 * @param consolePlayer
	 */
	private static void letOpponentMove(DomineeringBoard board){
		CommandLineDom consolePlayer = new CommandLineDom();
		DomineeringMove m = consolePlayer.getMove();//Get move from player2(console)
		board = (DomineeringBoard) board.play(m);
		board.tree(0).firstPlayer(consolePlayer);
	}

	public static void main(String[] args) {
		assert (args.length == 4);

		DomineeringBoard board;
		int width = Integer.parseInt(args[2]);
		int height = Integer.parseInt(args[3]);
		
		switch(args[0]){
			case "first":
				switch(args[1]){
					case "horizontal":
						// PLayerH starts
						board = playOptimally(width, height) ? new DomineeringBoard(-1, true, true, width, height) : new DomineeringBoard(exploredLimit, true, true, width, height);
						//System.out.println("Benchmark Started making first board");
						long time = System.currentTimeMillis();
						board.tree(0);
						time = System.currentTimeMillis() - time;
						//System.out.println("Benchmark Finished making first board");
						//System.out.println("Benchmark time " + time);
						
						//System.out.println("Now making board for playing from scratch");
						board.tree(0).firstPlayer(new CommandLineDom());
						break;
					case "vertical":
						board = playOptimally(width, height) ? new DomineeringBoard(-1, true, false, width, height) : new DomineeringBoard(exploredLimit, true, false, width, height);// PLayerH starts
						board.tree(0).firstPlayer(new CommandLineDom());
						break;
					default:
						System.exit(1);	
				}
			break;
			case "second"://User starts so wait for first move THEN calculate possibilities
				switch(args[1]){
					case "horizontal":
						board = playOptimally(width, height) ? new DomineeringBoard(-1, false, true, width, height) : new DomineeringBoard(exploredLimit, false, true, width, height);// PLayerH starts
						
						//opponent moves first
						letOpponentMove(board);
					break;
					case "vertical":
						board = playOptimally(width, height) ? new DomineeringBoard(-1, false, false, width, height) : new DomineeringBoard(exploredLimit, false, false, width, height);// PLayerH starts
						
						//opponent moves first
						letOpponentMove(board);
						break;
					default:
						System.exit(1);	
				}
				break;
			default:
				System.exit(1);	
		}
	}
	
	/**
	 * Only play optimally if the board is smaller than or equal to a certain size
	 * @param boardWidth
	 * @param boardHeight
	 * @return
	 */
	private static boolean playOptimally(int boardWidth, int boardHeight) {
		return boardWidth <= 4 && boardHeight <= 5 || boardWidth <= 5 && boardHeight <= 4;
	}
}
