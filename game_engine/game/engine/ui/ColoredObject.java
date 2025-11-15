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
 * Represents a colored UI object.
 */
public abstract class ColoredObject extends GameObject {

  /** color of the object */
  public final Color color;
  /**
   * Creates an colored object.
   * @param x {@link GameObject#x}
   * @param y {@link GameObject#y}
   * @param width {@link GameObject#width}
   * @param height {@link GameObject#height}
   * @param color color of the object
   */
  public ColoredObject(int x, int y, int width, int height, Color color) {
    super(x, y, width, height, null);
    this.color = color;
  }
  
  @Override
  public abstract void draw(Graphics graphics, GameCanvas canvas);

}
