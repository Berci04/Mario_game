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

package game.engine.utils;

import java.lang.management.ManagementFactory;
import java.util.List;

import game.engine.Action;
import game.engine.Player;

/**
 * Calls the {@link Player#getAction(List, long[])} method of the player that was set in 
 * and returns its result and measures the elapsed time in nanoseconds.
 */
public final class ActionTask implements TimeOutTask<Action> {
  private Player<Action> player;
  private List<Pair<Integer, Action>> prevAction;
  private long elapsed;
  private long[] remainings;

  /**
   * Sets the parameters to get the action after running the task.
   * @param player who will create the action
   * @param prevAction previous actions of other players
   * @param remainings remaining times
   */
  public void setParams(Player<Action> player, List<Pair<Integer, Action>> prevAction, long[] remainings) {
    this.player = player;
    this.prevAction = prevAction;
    this.remainings = remainings;
  }

  @Override
  public long getElapsed() {
    return elapsed;
  }

  @Override
  public Action call() throws Exception {
    long start = ManagementFactory.getThreadMXBean().getThreadUserTime(Thread.currentThread().getId());
    Action result = player.getAction(prevAction, remainings);
    elapsed = (ManagementFactory.getThreadMXBean().getThreadUserTime(Thread.currentThread().getId()) - start);
    return result;
  }

}
