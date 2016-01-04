package de.adrodoc55.minecraft.mpl;

import static de.adrodoc55.minecraft.Coordinate3D.Direction.EAST;
import static de.adrodoc55.minecraft.Coordinate3D.Direction.SOUTH;
import static de.adrodoc55.minecraft.Coordinate3D.Direction.UP;

import de.adrodoc55.minecraft.Coordinate3D.Axis;
import de.adrodoc55.minecraft.Coordinate3D.Direction;

public class MplOrientation {

    private Direction a;
    private Direction b;
    private Direction c;

    public MplOrientation() {
        this(EAST, UP, SOUTH);
    }

    public MplOrientation(Direction a, Direction b, Direction c) {
        setValue(a, b, c);
    }

    private void setValue(Direction a, Direction b, Direction c) {
        Axis aAxis = a.getAxis();
        Axis bAxis = b.getAxis();
        Axis cAxis = c.getAxis();
        if (aAxis == bAxis || bAxis == cAxis || cAxis == aAxis) {
            throw new IllegalArgumentException("All Directions must be on different Axis!");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Direction getA() {
        return a;
    }

    public Direction getB() {
        return b;
    }

    public Direction getC() {
        return c;
    }
}
