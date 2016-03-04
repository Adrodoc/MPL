package de.adrodoc55.minecraft.mpl

import static de.adrodoc55.minecraft.Coordinate3D.Direction.*
import org.junit.Test

import spock.lang.Specification
import spock.lang.Unroll;
import de.adrodoc55.minecraft.Coordinate3D.Direction

class OrientationSpec extends Specification {

  @Test
  @Unroll('String construcor (#param)')
  public void "String construcor"(String param, Direction a, Direction b, Direction c) {
    when:
    MplOrientation ori = new MplOrientation(param);
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

}
