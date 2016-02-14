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

import com.google.common.collect.Iterables;

@lombok.AllArgsConstructor(access = lombok.AccessLevel.PACKAGE)
public class Directions implements DirectionIterable {
  private final Iterable<Direction> directions;

  @Override
  public Iterator<Direction> iterator() {
    return directions.iterator();
  }

  public Directions repeat(int n) {
    return new Directions(Iterables.concat(Iterables.limit(Iterables.cycle(directions), n)));
  }

  public Directions repeat() {
    return new Directions(Iterables.cycle(directions));
  }

  public static Directions $(DirectionIterable... args) {
    return new Directions(Iterables.concat(args));
  }
}
