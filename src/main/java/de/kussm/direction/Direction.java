/*
 * Copyright 2015-2016 by the authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.kussm.direction;

import java.util.Iterator;

import com.google.common.collect.Iterators;

/**
 * @author Michael Kuß
 */
public enum Direction implements DirectionIterable {
  NORTH,
  EAST,
  SOUTH,
  WEST;

  @Override
  public Iterator<Direction> iterator() {
    return Iterators.forArray(this);
  }

  public Directions repeat(int n) {
    return new Directions(this).repeat(n);
  }

  public Directions repeat() {
    return new Directions(this).repeat();
  }
}
