package game.mario.players;

import java.util.Random;

import game.mario.Direction;
import game.mario.MarioPlayer;
import game.mario.ui.MarioCanvas;
import game.mario.utils.MarioState;

/**
 * Used for manual controlled Mario only with GUI.
 */
public class HumanPlayer extends MarioPlayer {

  public HumanPlayer(int color, Random random, MarioState state) {
    super(color, random, state);
  }

  @Override
  public Direction getDirection(long remainingTime) {
    return MarioCanvas.getLastAction();
  }

}
