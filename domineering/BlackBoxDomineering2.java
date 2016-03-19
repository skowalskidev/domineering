package domineering;

/**
 * TODO Change to proper BBD2 temporarily this is used for testing
 * @author Szymon
 *
 */
public class BlackBoxDomineering2 {
	public static class CommandLineDom implements MoveChannel<DomineeringMove> {
		@Override
		public DomineeringMove getMove() {
			String input = System.console().readLine("Enter your move: ");
			assert (input.length() == 3);//Input format: x y
			return new DomineeringMove(Character.getNumericValue(input.charAt(0)), Character.getNumericValue(input.charAt(2)));
		}

		public void giveMove(DomineeringMove move) {
			System.out.println("I play " + move);
		}

		public void comment(String msg) {
			System.out.println(msg);
		}

		public void end(int value) {
			System.out.println("Game over. The result is " + value);
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
		board.tree().firstPlayer(consolePlayer);
	}

	public static void main(String[] args) {
		assert (args.length == 4);

		DomineeringBoard board;
		
		switch(args[0]){
			case "first":
				switch(args[1]){
					case "horizontal":
						board = new DomineeringBoard(true, true, Integer.parseInt(args[2]), Integer.parseInt(args[3]));// PLayerH starts
						board.tree().firstPlayer(new CommandLineDom());
					break;
					case "vertical":
						board = new DomineeringBoard(true, false, Integer.parseInt(args[2]), Integer.parseInt(args[3]));// PLayerH starts
						board.tree().firstPlayer(new CommandLineDom());
						break;
					default:
						System.exit(1);	
				}
			break;
			case "second"://User starts so wait for first move THEN calculate possibilities
				switch(args[1]){
					case "horizontal":
						board = new DomineeringBoard(false, true, Integer.parseInt(args[2]), Integer.parseInt(args[3]));// PLayerH starts
						//board.tree().secondPlayer(new CommandLineDom());
						
						//opponent moves first
						letOpponentMove(board);
					break;
					case "vertical":
						board = new DomineeringBoard(false, false, Integer.parseInt(args[2]), Integer.parseInt(args[3]));// PLayerH starts
						//board.tree().secondPlayer(new CommandLineDom());
						
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
}
