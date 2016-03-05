package de.adrodoc55.minecraft.coordinate

import static de.adrodoc55.minecraft.coordinate.Direction3D.*

import org.junit.Test

import spock.lang.Specification
import spock.lang.Unroll

class Orientation3DSpec extends Specification {

  @Test
  @Unroll('String construcor (#param)')
  public void 'String construcor'(String param, Direction3D a, Direction3D b, Direction3D c) {
    when:
    Orientation3D ori = new Orientation3D(param);
    then:
    ori.a == a;
    ori.b == b;
    ori.c == c;
    where:
    param   | a     | b     | c
    'xyz'   | EAST  | UP    | SOUTH
    '-xyz'  | WEST  | UP    | SOUTH
    'x-yz'  | EAST  | DOWN  | SOUTH
    'xy-z'  | EAST  | UP    | NORTH
    '-x-yz' | WEST  | DOWN  | SOUTH
    '-xy-z' | WEST  | UP    | NORTH
    'x-y-z' | EAST  | DOWN  | NORTH
    '-x-y-z'| WEST  | DOWN  | NORTH

    'yxz'   | UP    | EAST  | SOUTH
    'y-xz'  | UP    | WEST  | SOUTH
    '-yxz'  | DOWN  | EAST  | SOUTH
    'yx-z'  | UP    | EAST  | NORTH
    '-y-xz' | DOWN  | WEST  | SOUTH
    'y-x-z' | UP    | WEST  | NORTH
    '-yx-z' | DOWN  | EAST  | NORTH
    '-y-x-z'| DOWN  | WEST  | NORTH

    'zxy'   | SOUTH | EAST  | UP
    'z-xy'  | SOUTH | WEST  | UP
    'zx-y'  | SOUTH | EAST  | DOWN
    '-zxy'  | NORTH | EAST  | UP
    'z-x-y' | SOUTH | WEST  | DOWN
    '-z-xy' | NORTH | WEST  | UP
    '-zx-y' | NORTH | EAST  | DOWN
    '-z-x-y'| NORTH | WEST  | DOWN
  }

  @Test
  @Unroll('Exceptions (#param)')
  public void 'Exceptions'(String param, String message) {
    when:
    new Orientation3D(param);
    then:
    IllegalArgumentException ex = thrown()
    ex.message == message
    where:
    param   | message
    'abc'   | "Unknown direction 'a'!"
    'x-zg'  | "Unknown direction 'g'!"
    'x-zyg' | "Unknown direction 'g'!"
    'xy+z'  | "Unknown direction '+'!"
    'xy-f'  | "Unknown direction '-f'!"
    'x-y'   | "An orientation must contain 3 directions!"
    'xyzy'  | "An orientation must contain 3 directions!"
    'xxx'   | "All directions must be on different axis!"
    'z-x-z' | "All directions must be on different axis!"
    '-'     | "Every '-' must be followed by an axis!"
    'xyz-'  | "Every '-' must be followed by an axis!"
  }
}
