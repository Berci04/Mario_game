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

package game.engine;

import java.util.List;

import game.engine.utils.Pair;

/**
 * Describes players of the games and the type of actions, can be performed.
 * 
 * @param <A> type of actions
 */
public interface Player<A extends Action> {
  
  /**
   * Returns the next action of a player.
   * @param prevActions list of action history that are pairs of player color 
   *     and action. Contains the enemy actions for the previous round only.
   * @param remainingTimes the overall remaining times of the players in the 
   *     game, indexed by the color of the players
   * @return next action
   */
  public A getAction(List<Pair<Integer, A>> prevActions, long[] remainingTimes);
  
  /**
   * Returns the color of a player.
   * @return color
   */
  public int getColor();
}
