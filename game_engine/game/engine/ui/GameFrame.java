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
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;

/**
 * This class handles the frame of the GUI.
 */
public final class GameFrame extends JFrame {
  private static final long serialVersionUID = -7338487637241129860L;
  
  /**
   * Constructs the frame based on the specified parameters.
   * @param title the tile of the window
   * @param iconPath the icon path of the window
   * @param canvas the object that paints the inner side of the window
   */
  public GameFrame(String title, String iconPath, GameCanvas canvas) {
    super(title);
    Image img = Toolkit.getDefaultToolkit().getImage(GameFrame.class.getResource(iconPath));
    setIconImage(img);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    
    // center of the screen
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension canvasSize = canvas.getPreferredSize();
    setLocation((screenSize.width - canvasSize.width) / 2, (screenSize.height - canvasSize.height) / 2);
    
    // add canvas (panel)
    add(canvas);
    // stretch frame to canvas
    pack();
    
    setVisible(true);
  }
}
