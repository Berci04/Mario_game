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

import java.awt.Graphics;
import java.awt.Image;

/**
 * GUI related class. A game object has a position (x, y), 
 * dimensions(width, height) and an image to be rendered.
 */
public class GameObject {

  /** Horizontal position of the object. */
  public final int x;
  /** Vertical position of the object. */
  public final int y;
  /** Width of the object. */
  public final int width;
  /** Height of the object. */
  public final int height;
  /** Look of the object. */
  public final Image image;

  /**
   * Creates a game object from the specifid parameters.
   * @param x horizontal position
   * @param y vertical position
   * @param width width of the object
   * @param height height of the object
   * @param image look of the object
   */
  public GameObject(int x, int y, int width, int height, Image image) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.image = image;
  }
  
  /**
   * Draws the current object on the specified canvas using the specified graphics.
   * @param graphics used to draw
   * @param canvas draw on
   */
  public void draw(Graphics graphics, GameCanvas canvas) {
    graphics.drawImage(image, x, y, width, height, canvas);
  }
  
  @Override
  public String toString() {
    return x + " " + y + " " + width + " " + height + " " + image;
  }
}
