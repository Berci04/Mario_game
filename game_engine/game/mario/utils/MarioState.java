package game.mario.utils;

import game.engine.utils.Utils;
import game.mario.Direction;
import game.mario.MarioGame;

/**
 * Represents the state of the {@link MarioGame}
 */
public class MarioState {
  /** map of the game */
  public final int[][] map;
  /** Mario */
  public final Mario mario;
  /** start column position of Mario */
  public final double startStage;
  /** distance has been taken by Mario */
  public double distance;
  /** is Mario jumping */
  public boolean isInAir;
  /** score of the player */
  public double score;
  /**
   * Creates a game state and sets the map and player 
   * @param map map to be set
   * @param mario player to be set
   */
  public MarioState(int[][] map, Mario mario) {
    this.map = map;
    this.mario = mario;
    this.isInAir = false;
    startStage = mario.j;
    distance = 0;
    score = 0;
  }
  /**
   * Copy constructor of the game state
   * @param state to be cloned
   */
  public MarioState(MarioState state) {
    map = Utils.copy(state.map);
    mario = new Mario(state.mario);
    startStage = state.startStage;
    distance = state.distance;
    isInAir = state.isInAir;
    score = state.score;
  }
  /**
   * Applies the specified direction action on the state and returns false if the game is over.
   * @param direction action to be applied, set null if do nothing
   * @return false if the game is over
   */
  public boolean apply(Direction direction) {
    int action = direction == null ? -1 : direction.direction;
    map[(int)mario.i][(int)mario.j] = MarioGame.EMPTY;
    double ai = 0;
    double aj = 0;
    switch (action) {
    case MarioGame.RIGHT:
      aj = MarioGame.AJ;
      break;
    case MarioGame.UP:
      if (!isInAir) {
        ai = -MarioGame.AI;
        isInAir = true;
      }
      break;
    case MarioGame.LEFT:
      aj = -MarioGame.AJ;
      break;
    default:
      break;
    }
    double sign = Math.signum(mario.vj);
    mario.update(ai + MarioGame.RELEASEI, aj - (isInAir ? 0 : sign*MarioGame.RELEASEJ));
    if (sign != 0 && sign != Math.signum(mario.vj)) {
      mario.vj = 0;
    }
    // out of map
    if (mario.j <= 0) mario.j = 0;
    if (mario.i <= 0) mario.i = 0;
    // fall down or reach the end
    if (MarioGame.H-1 <= mario.i || MarioGame.W-1 <= mario.j) {
      return false;
    }
    // check collisions
    // horizontally
    // front of a wall
    if (MarioGame.W-1 < mario.j || 
        map[(int)mario.i][(int)(mario.j+1)] == MarioGame.WALL || 
        map[(int)mario.i][(int)(mario.j+1)] == MarioGame.PIPE || 
        map[(int)mario.i][(int)(mario.j+1)] == MarioGame.SURPRISE) {
      mario.j = (int)mario.j;
      mario.vj = 0;
    }
    // wall behind
    if (map[(int)mario.i][(int)(mario.j)] == MarioGame.WALL || 
        map[(int)mario.i][(int)(mario.j)] == MarioGame.PIPE || 
        map[(int)mario.i][(int)(mario.j)] == MarioGame.SURPRISE) {
      mario.j = (int)mario.j+1;
      mario.vj = 0;
    }
    // vertically
    // on a wall
    if (map[1+(int)mario.i][(int)(mario.j+0.5)] == MarioGame.WALL || 
        map[1+(int)mario.i][(int)(mario.j+0.5)] == MarioGame.PIPE || 
        map[1+(int)mario.i][(int)(mario.j+0.5)] == MarioGame.SURPRISE) {
      mario.i = (int)mario.i;
      mario.vi = 0;
      isInAir = false;
    }
    // under a wall
    if (1 <= mario.i && 
        (map[(int)Math.round(mario.i+mario.vi-0.5)][(int)(mario.j+0.5)] == MarioGame.WALL || 
        map[(int)Math.round(mario.i+mario.vi-0.5)][(int)(mario.j+0.5)] == MarioGame.PIPE)) {
      mario.i = (int)mario.i;
      mario.vi = MarioGame.RELEASEI;
    }
    // under surprise
    if (1 <= mario.i && (map[(int)Math.round(mario.i+mario.vi-0.5)][(int)(mario.j+0.5)] == MarioGame.SURPRISE)) {
      map[(int)Math.round(mario.i+mario.vi-0.5)][(int)(mario.j+0.5)] = MarioGame.WALL;
      score += MarioGame.SURPRISE_PRICE;
      mario.i = (int)mario.i;
      mario.vi = 0;
    }
    // check coin
    if (map[(int)(mario.i)][(int)(mario.j+0.5)] == MarioGame.COIN) {
      map[(int)(mario.i)][(int)(mario.j+0.5)] = MarioGame.EMPTY;
      score += MarioGame.COIN_PRICE;
    }
    // reposition
    map[(int)mario.i][(int)mario.j] = MarioGame.MARIO;
    // distance score
    if (distance < mario.j - startStage) {
      score += MarioGame.DISTANCE_PRICE * (mario.j - startStage - distance);
      distance = mario.j - startStage;
    }
    return true;
  }
  @Override
  public String toString() {
    return MarioGame.map2str(map) + "MARIO: " + mario + ", SCORE: " + score;
  }
}
