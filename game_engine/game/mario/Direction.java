package game.mario;

import game.engine.Action;

/**
 * Represents the action of the {@link MarioGame}
 */
public class Direction implements Action {
  private static final long serialVersionUID = -3063079145872719211L;
  
  /** the direction of the action  */
  public final int direction;
  /**
   * Creates an action by the specified direction
   * @see possible values {@link MarioGame#DIRECTIONS}
   * @param direction to be set
   */
  public Direction(int direction) {
    this.direction = direction;
  }
  
  @Override
  public String toString() {
    return MarioGame.DIRECTION_STRINGS[direction];
  }

}
