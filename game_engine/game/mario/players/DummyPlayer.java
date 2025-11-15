package game.mario.players;

import java.util.Random;

import game.mario.Direction;
import game.mario.MarioPlayer;
import game.mario.utils.MarioState;

/**
 * Used for replay mode.
 */
public class DummyPlayer extends MarioPlayer {

  public DummyPlayer(int color, Random random, MarioState state) {
    super(color, random, state);
  }

  @Override
  public Direction getDirection(long remainingTime) {
    return null;
  }

}
