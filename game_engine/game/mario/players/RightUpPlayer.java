package game.mario.players;

import java.util.Random;

import game.mario.Direction;
import game.mario.MarioGame;
import game.mario.MarioPlayer;
import game.mario.utils.MarioState;

/**
 * Randomly chooses {@link MarioGame#RIGHT} or {@link MarioGame#UP} action.
 */
public class RightUpPlayer extends MarioPlayer {

  public RightUpPlayer(int color, Random random, MarioState state) {
    super(color, random, state);
  }

  @Override
  public Direction getDirection(long remainingTime) {
    Direction action = new Direction(MarioGame.DIRECTIONS[random.nextInt(MarioGame.DIRECTIONS.length-1)]);
    state.apply(action);
    return action;
  }

}
