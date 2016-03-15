package domineering;


interface MoveChannel<Move> {
  public Move getMove();
  public void giveMove(Move move);
  public void end(int Value);
  public void comment(String msg);
}

// Is this correct level of abstraction?
// (I haven't tested it with a GUI yet.)
