package game.mario;

import java.util.List;
import java.util.Random;

import game.engine.Player;
import game.engine.utils.Pair;
import game.mario.utils.MarioState;

/**
 * Abstract class the specifies a player of the {@link MarioGame}
 */
public abstract class MarioPlayer implements Player<Direction> {
  
  /** color of the player - not used, inherited */
  public final int color;
  /** random generator object */
  public final Random random;
  /** game state */
  public final MarioState state;
  
  /**
   * Creates a player of the game
   * @param color player color
   * @param random random generator
   * @param state game state
   */
  public MarioPlayer(int color, Random random, MarioState state) {
    this.color = color;
    this.random = random;
    this.state = state;
  }

  /**
   * Returns the next action of the player as a {@link Direction} of null if do nothing
   * @param remainingTime the remaining time for thinking
   * @return action to be applied on the game state
   */
  public abstract Direction getDirection(long remainingTime);
  
  @Override
  public final Direction getAction(List<Pair<Integer, Direction>> prevActions, long[] remainingTimes) {
    return getDirection(remainingTimes[color]);
  }

  @Override
  public final int getColor() {
    return color;
  }
  
  @Override
  public final String toString() {
    return getClass().getCanonicalName();
  }

}
