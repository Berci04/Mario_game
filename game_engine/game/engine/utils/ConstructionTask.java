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
import java.lang.reflect.Constructor;

/**
 * Constructs the specified type of object calling the constructor was set with 
 * the parameters were set and measures the elapsed time in nanoseconds.
 * @param <R> type of object to be constructed
 */
public class ConstructionTask<R> implements TimeOutTask<R> {
  private Constructor<R> constructor;
  private Object[] params;
  private long elapsed;

  public void setConstructor(Constructor<R> constructor, Object... params) {
    this.constructor = constructor;
    this.params = params;
  }

  @Override
  public long getElapsed() {
    return elapsed;
  }

  @Override
  public R call() throws Exception {
    long start = ManagementFactory.getThreadMXBean().getThreadUserTime(Thread.currentThread().getId());
    R result = constructor.newInstance(params);
    elapsed = (ManagementFactory.getThreadMXBean().getThreadUserTime(Thread.currentThread().getId()) - start);
    return result;
  }

}
