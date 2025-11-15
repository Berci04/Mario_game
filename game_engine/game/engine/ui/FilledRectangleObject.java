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

/**
 * Represents a filled rectangle UI object.
 */
public class FilledRectangleObject extends ColoredObject {

  /**
   * Creates a filled rectangle object.
   * @param x {@link GameObject#x}
   * @param y {@link GameObject#y}
   * @param width {@link GameObject#width}
   * @param height {@link GameObject#height}
   * @param color fill color of the rectangle object
   */
  public FilledRectangleObject(int x, int y, int width, int height, Color color) {
    super(x, y, width, height, color);
  }
  
  public void draw(Graphics graphics, GameCanvas canvas) {
    graphics.setColor(color);
    graphics.fillRect(x, y, width, height);
  }

}
