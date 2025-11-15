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

import java.io.Serializable;

/**
 * Represents a pair of the specified type of objects.
 * @param <F> first object
 * @param <S> second object
 */
public class Pair<F, S> implements Serializable {
  private static final long serialVersionUID = -6613238923067231223L;
  public final F first;
  public final S second;

  /**
   * Constructs a pair of the specified objects.
   * @param first first object
   * @param second second object
   */
  public Pair(F first, S second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public String toString() {
    return Utils.jsonSerialize(this);
  }
}
