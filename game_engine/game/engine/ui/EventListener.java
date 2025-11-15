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

import java.awt.event.KeyEvent;

import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

/**
 * Event listener helper class for the GUI.
 * Handles all mouse and key events and calls the handle function with them.
 */
public abstract class EventListener extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
  private static final long serialVersionUID = 8947020176554115288L;
  /**
   * Construct class and sets listeners.
   */
  public EventListener() {
    setFocusable(true);
    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
  }
  
  /**
   * Called when key event is occurred and event is passed as parameter.
   * @param event the occurred key event
   */
  public abstract void handle(KeyEvent event);
  /**
   * Called when mouse event is occurred and event is passed as parameter.
   * @param event the occurred mouse event
   */
  public abstract void handle(MouseEvent event);
  
  @Override
  public final void keyPressed(KeyEvent event) {
    handle(event);
  }
  @Override
  public final void keyReleased(KeyEvent event) {
    handle(event);
  }
  @Override
  public final void keyTyped(KeyEvent event) {
    handle(event);
  }
  @Override
  public final void mouseDragged(MouseEvent event) {
    handle(event);
  }
  @Override
  public final void mouseMoved(MouseEvent event) {
    handle(event);
  }
  @Override
  public final void mouseClicked(MouseEvent event) {
    handle(event);
  }
  @Override
  public final void mouseEntered(MouseEvent event) {
    handle(event);
  }
  @Override
  public final void mouseExited(MouseEvent event) {
    handle(event);
  }
  @Override
  public final void mousePressed(MouseEvent event) {
    handle(event);
  }
  @Override
  public final void mouseReleased(MouseEvent event) {
    handle(event);
  }

}
