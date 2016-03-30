


interface MoveChannel<Move> {
  public Move getMove();
  /**
   * Show the move made to the player(console)
   * @param move
   */
  public void giveMove(Move move);
  public void end(int Value);
  public void comment(String msg);
}

// Is this correct level of abstraction?
// (I haven't tested it with a GUI yet.)
