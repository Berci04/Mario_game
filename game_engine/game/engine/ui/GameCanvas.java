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

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import game.engine.Action;

/**
 * Represents the canvas of a game, that is the inner side of the window.
 */
public abstract class GameCanvas extends EventListener {
  private static final long serialVersionUID = -8859803804306893647L;
  /**
   * The width and the height of the canvas.
   */
  protected final int width, height;
  /**
   * Used for handling interactions with the human players.
   */
  protected final static BlockingQueue<Action> QUEUE = new ArrayBlockingQueue<Action>(1, true);
  
  /**
   * Constructs the canvas and sets the specified dimensions.
   * @param width width of the canvas
   * @param height height of the canvas
   */
  public GameCanvas(int width, int height) {
    this.width = width;
    this.height = height;
    setPreferredSize(new Dimension(width, height));
  }
  
  /**
   * Paints the background of the canvas.
   * @param graphics used to draw
   */
  public abstract void paintBackground(Graphics graphics);
  /**
   * Paints the game objects to the canvas.
   * @param graphics used to draw
   */
  public abstract void paintObjects(Graphics graphics);
  
  @Override
  protected final void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    paintBackground(graphics);
    paintObjects(graphics);
  }
  
  /**
   * Returns the last action, was added.
   * @return last action
   * @throws InterruptedException {@link BlockingQueue}
   */
  public final static Action getAction() throws InterruptedException {
    return QUEUE.take();
  }
  
}
