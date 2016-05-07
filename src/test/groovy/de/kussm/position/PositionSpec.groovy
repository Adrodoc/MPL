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
package de.kussm.position

import static de.kussm.chain.ChainLinkType.*
import static de.kussm.direction.Direction.*
import spock.lang.Specification

/**
 * Tests for {@link Position}
 * @author Michael Kuß
 */
public class PositionSpec extends Specification {
  def 'neighbour(Direction)'() {
    expect:
    Position.at(1,3).neighbour(NORTH) == Position.at(1,4)
    Position.at(1,3).neighbour(EAST) == Position.at(2,3)
    Position.at(1,3).neighbour(SOUTH) == Position.at(1,2)
    Position.at(1,3).neighbour(WEST) == Position.at(0,3)
  }

  def 'neighbours()'() {
    expect:
    Position.at(1,3).neighbours() == [
      Position.at(1,4),
      Position.at(2,3),
      Position.at(1,2),
      Position.at(0,3) ] as Set
  }
}
