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
package de.kussm.position;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import lombok.NonNull;
import de.kussm.direction.Direction;
import static  de.kussm.direction.Direction.*;

/**
 * @author Michael Kuß
 */
@lombok.RequiredArgsConstructor(staticName="at")
@lombok.Getter
@lombok.EqualsAndHashCode
@lombok.ToString
public class Position {
  private final int x;
  private final int y;

  public Position neighbour(@NonNull Direction d) {
    switch (d) {
      case NORTH:
        return Position.at(x, y+1);
      case EAST:
        return Position.at(x+1, y);
      case SOUTH:
        return Position.at(x, y-1);
      case WEST:
        return Position.at(x-1, y);
    }
    throw new IllegalStateException("Unknown direction " + d);
  }

  public Set<Position> neighbours() {
    return ImmutableSet.<Position>of(neighbour(NORTH), neighbour(EAST), neighbour(SOUTH), neighbour(WEST));
  }
}
