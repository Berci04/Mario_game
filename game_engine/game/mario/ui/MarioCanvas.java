package game.mario.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import game.engine.ui.BoardGameCanvas;
import game.engine.ui.Drawable;
import game.mario.Direction;
import game.mario.MarioGame;

/**
 * Canvas for the {@link MarioGame}.
 */
public class MarioCanvas extends BoardGameCanvas {
  private static final long serialVersionUID = -6581687442191632078L;

  /** window height */
  public static final int HEIGHT = 360;
  private static Direction lastAction;
  
  public MarioCanvas(int n, int m, Drawable game) {
    super(n, m, HEIGHT/n, game);
    lastAction = null;
  }
  
  public void pressed(KeyEvent event) {
    switch (event.getKeyCode()) {
    case 37:
      lastAction = new Direction(MarioGame.LEFT);
      break;
    case 39:
      lastAction = new Direction(MarioGame.RIGHT);
      break;
    case 38:
      lastAction = new Direction(MarioGame.UP);
      break;
    case 40:
      break;
    default:
      break;
    }
  }
  
  @Override
  public void paintBackground(Graphics graphics) {
    graphics.setColor(new Color(92, 148, 252));
    graphics.fillRect(0, 0, m*multiplier, n*multiplier);
  }
  
  @Override
  public void handle(MouseEvent event) {
  }
  
  @Override
  public void setCoordinates() {
  }
  
  public static final Direction getLastAction() {
    Direction result = lastAction;
    lastAction = null;
    return result;
  }

}
