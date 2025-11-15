package game.mario.utils;

import game.mario.MarioGame;

/**
 * Represents the Mario of the {@link MarioGame}
 */
public class Mario {
  /** position of the Mario, matrix indexing is used (i - row index, j - column index) */
  public double i, j;
  /** speed of the Mario */
  public double vi, vj;
  /**
   * Constructs a Mario at the specified position.
   * @param i row index
   * @param j column index
   */
  public Mario(double i, double j) {
    this.i = i;
    this.j = j;
    vi = MarioGame.RELEASEI;
    vj = 0.0;
  }
  /**
   * Copy constructor.
   * @param mario to be cloned
   */
  public Mario(Mario mario) {
    i = mario.i;
    j = mario.j;
    vi = mario.vi;
    vj = mario.vj;
  }
  /**
   * Updates the speed and position of the Mario by the specified acceleration values.
   * @param ai row acceleration
   * @param aj column acceleration
   */
  public void update(double ai, double aj) {
    vi += ai;
    vj += aj;
    vi = Math.max(Math.min(vi, MarioGame.MAX_VI), -MarioGame.MAX_VI);
    vj = Math.max(Math.min(vj, MarioGame.MAX_VJ), -MarioGame.MAX_VJ);
    i += vi;
    j += vj;
  }
  @Override
  public String toString() {
    return "P:(" + i + " " + j + "), V:(" + vi + " " + vj + ")";
  }
}
