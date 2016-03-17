package domineering;


import java.io.*;
import java.util.*;
import java.lang.*;
import java.lang.Integer;

public abstract class Board<Move> {
  abstract Player nextPlayer();
  abstract Set<Move> availableMoves(); 
  abstract int value(); 
  abstract Board<Move> play(Move move);

  // Constructs the game tree of the board using the minimax algorithm
  // (without alpha-beta pruning):
  public GameTree<Move> tree() {
    if (availableMoves().isEmpty())
      return new GameTree<Move>
                    (this, 
                     new LinkedHashMap<Move,GameTree<Move>>(), 
                     value());
    else
      return (nextPlayer() == Player.MAXIMIZER ? maxTree() : minTree());
  }

  // Two helper methods for that, which call the above method tree:
  public GameTree<Move> maxTree() {
    assert(!availableMoves().isEmpty());

    int optimalOutcome = Integer.MIN_VALUE;
    LinkedHashMap<Move,GameTree<Move>> children 
                 = new LinkedHashMap<Move,GameTree<Move>>(); 

    for (Move m : availableMoves()) {
      GameTree<Move> subtree = play(m).tree();
      children.put(m,subtree);
      optimalOutcome = Math.max(optimalOutcome,subtree.optimalOutcome());
    }

    return new GameTree<Move>(this,children,optimalOutcome); 
  }

  public GameTree<Move> minTree() {
    assert(!availableMoves().isEmpty());

    int optimalOutcome = Integer.MAX_VALUE;
    LinkedHashMap<Move,GameTree<Move>> children 
                 = new LinkedHashMap<Move,GameTree<Move>>(); 

    for (Move m : availableMoves()) {
      GameTree<Move> subtree = play(m).tree();
      children.put(m,subtree);
      optimalOutcome = Math.min(optimalOutcome,subtree.optimalOutcome());
    }

    return new GameTree<Move>(this,children,optimalOutcome); 
  }
}
