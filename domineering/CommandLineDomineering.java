

/**
 * Usage java CommandLineDomineering boardWidth boardHeight
 * @author Szymon
 *
 */
/*public class CommandLineDomineering{

	private static class CommandLineDom implements MoveChannel<DomineeringMove> {
		@Override
		public DomineeringMove getMove() {
			String input = System.console().readLine("Enter your move: ");
			assert(input.length() == 3);//Input format: x y
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

	public static void main(String [] args) {
		//String [] args = {"2","2"};
	    DomineeringBoard board = new DomineeringBoard(true ,true, Integer.parseInt(args[0]), Integer.parseInt(args[1]));//PLayerH Starts
	   board.tree().firstPlayer(new CommandLineDom());
	     // board.tree().secondPlayer(new CommandLineDom());
	  }
}
*/