/*
 * Copyright (c) 2008 University of Szeged
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package game.engine.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Specifies the background painting by drawing a grid on the canvas and 
 * handles mouse and keyboard interactions.
 */
public abstract class BoardGameCanvas extends GameCanvas {
  private static final long serialVersionUID = 9001309005102687932L;
  /** Scales the canvas. */
  public final int multiplier;
  /** Specify the number of rows and columns of the board. */
  protected final int n, m;
  /** the x position of the cursor on the grid. */
  protected int lastx = -1;
  /** the y position of the cursor on the grid. */
  protected int lasty = -1;
  /** the x position of the cursor. */
  protected int x = -1;
  /** the y position of the cursor. */
  protected int y = -1;
  /** the game object that presents the objects to be drawn. */
  protected Drawable game;
  
  /**
   * Constructs the grid canvas with the specified parameters.
   * @param n number of row
   * @param m number of columns
   * @param multiplier scale factor for the canvas
   * @param game to get objects from
   */
  public BoardGameCanvas(int n, int m, int multiplier, Drawable game) {
    super(m * multiplier, n * multiplier);
    this.n = n;
    this.m = m;
    this.multiplier = multiplier;
    this.game = game;
  }
  
  @Override
  public void paintBackground(Graphics graphics) {
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(j * multiplier, i * multiplier, multiplier, multiplier);
        graphics.setColor(Color.GRAY);
        graphics.drawRect(j * multiplier, i * multiplier, multiplier, multiplier);
      }
    }
  }
  
  @Override
  public void paintObjects(Graphics graphics) {
    graphics.setColor(Color.RED);
    graphics.fillRect(lastx * multiplier + 1, lasty * multiplier + 1, multiplier - 1, multiplier - 1);
    for (GameObject object : game.getGameObjects()) {
      object.draw(graphics, this);
    }
  }
  
  @Override
  public void handle(KeyEvent event) {
    switch (event.getID()) {
    case KeyEvent.KEY_PRESSED:
      pressed(event);
      break;
    default:
      break;
    }
    repaint();
  }
  
  public void handle(MouseEvent event) {
    switch (event.getID()) {
    case MouseEvent.MOUSE_EXITED:
      mouseExit(event);
      break;
    case MouseEvent.MOUSE_MOVED:
      mouseMove(event);
      break;
    case MouseEvent.MOUSE_CLICKED:
      setCoordinates();
    default:
      break;
    }
    repaint();
  }
  
  /**
   * Called on key pressed, event is passed as the parameter.
   * @param event key event
   */
  protected void pressed(KeyEvent event) {
    int dx = event.getKeyCode() == 37 ? -1 : event.getKeyCode() == 39 ? 1 : 0;
    int dy = event.getKeyCode() == 38 ? -1 : event.getKeyCode() == 40 ? 1 : 0;
    int x = (lastx + dx + m) % m;
    int y = (lasty + dy + n) % n;
    lastx = x;
    lasty = y;
    if (event.getKeyCode() == 32) {
      setCoordinates();
    }
  }

  /**
   * Called on mouse canvas exit, event is passed as the parameter.
   * @param event mouse event
   */
  protected void mouseExit(MouseEvent event) {
    lastx = -1;
    lasty = -1;
    x = -1;
    y = -1;
  }

  /**
   * Called on mouse moved, event is passed as the parameter.
   * @param event mouse event
   */
  protected void mouseMove(MouseEvent event) {
    x = event.getX();
    y = event.getY();
    lastx = x / multiplier;
    lasty = y / multiplier;
  }
  
  /**
   * Method to handle interactions, called on mouse clicked or spase pressed.
   */
  public abstract void setCoordinates();

}
