package game.mario.players;

import java.util.Random;

import game.mario.Direction;
import game.mario.MarioGame;
import game.mario.MarioPlayer;
import game.mario.utils.MarioState;

/**
 * Takes a random action every time.
 */
public class RandomPlayer extends MarioPlayer {

  public RandomPlayer(int color, Random random, MarioState state) {
    super(color, random, state);
  }

  @Override
  public Direction getDirection(long remainingTime) {
    Direction action = new Direction(MarioGame.DIRECTIONS[random.nextInt(MarioGame.DIRECTIONS.length)]);
    state.apply(action);
    return action;
  }

}
