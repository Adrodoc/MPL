package de.adrodoc55.minecraft;

import static de.adrodoc55.TestBase.someInt;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class Coordinate3DTest {

	@Test
	public void test_newCoordinate3D() {
		// When:
		Coordinate3D c = new Coordinate3D();
		// Then:
		assertThat(c.getX()).isZero();
		assertThat(c.getY()).isZero();
		assertThat(c.getZ()).isZero();
	}

	@Test
	public void test_getX() {
		// Given:
		int x = someInt();
		// When:
		Coordinate3D c = new Coordinate3D(x, someInt(), someInt());
		// Then:
		assertThat(c.getX()).isEqualTo(x);
	}

	@Test
	public void test_getY() {
		// Given:
		int y = someInt();
		// When:
		Coordinate3D c = new Coordinate3D(someInt(), y, someInt());
		// Then:
		assertThat(c.getY()).isEqualTo(y);
	}

	@Test
	public void test_getZ() {
		// Given:
		int z = someInt();
		// When:
		Coordinate3D c = new Coordinate3D(someInt(), someInt(), z);
		// Then:
		assertThat(c.getZ()).isEqualTo(z);
	}

	@Test
	public void test_equals() {
		// Given:
		int x = someInt();
		int y = someInt();
		int z = someInt();
		Coordinate3D c1 = new Coordinate3D(x, y, z);
		Coordinate3D c2 = new Coordinate3D(x, y, z);
		// Expect:
		assertThat(c1).isEqualTo(c2);
	}

	@Test
	public void test_newCoordinate3D_Coordinate3D() {
		// Given:
		int x = someInt();
		int y = someInt();
		int z = someInt();
		Coordinate3D c = new Coordinate3D(x, y, z);
		// When:
		Coordinate3D actual = new Coordinate3D(c);
		// Then:
		assertThat(actual).isEqualTo(c);
	}

	@Test
	public void test_copy() {
		// Given:
		int x = someInt();
		int y = someInt();
		int z = someInt();
		Coordinate3D c = new Coordinate3D(x, y, z);
		// When:
		Coordinate3D actual = c.copy();
		// Then:
		assertThat(actual).isNotSameAs(c);
		assertThat(actual).isEqualTo(c);
	}

	@Test
	public void test_toAbsoluteString() {
		// Given:
		int x = someInt();
		int y = someInt();
		int z = someInt();
		Coordinate3D c = new Coordinate3D(x, y, z);
		// When:
		String absoluteString = c.toAbsoluteString();
		// Then:
		assertThat(absoluteString).isEqualTo(x + " " + y + " " + z);
	}

	@Test
	public void test_toRelativeString() {
		// Given:
		int x = someInt();
		int y = someInt();
		int z = someInt();
		Coordinate3D c = new Coordinate3D(x, y, z);
		// When:
		String relativeString = c.toRelativeString();
		// Then:
		assertThat(relativeString).isEqualTo("~" + x + " ~" + y + " ~" + z);
	}

	@Test
	public void test_plus() {
		// Given:
		int x1 = someInt();
		int y1 = someInt();
		int z1 = someInt();
		Coordinate3D c1 = new Coordinate3D(x1, y1, z1);
		int x2 = someInt();
		int y2 = someInt();
		int z2 = someInt();
		Coordinate3D c2 = new Coordinate3D(x2, y2, z2);
		// When:
		Coordinate3D actual = c1.plus(c2);
		// Then:
		assertThat(actual.getX()).isEqualTo(x1 + x2);
		assertThat(actual.getY()).isEqualTo(y1 + y2);
		assertThat(actual.getZ()).isEqualTo(z1 + z2);
	}

	@Test
	public void test_minus() {
		// Given:
		int x1 = someInt();
		int y1 = someInt();
		int z1 = someInt();
		Coordinate3D c1 = new Coordinate3D(x1, y1, z1);
		int x2 = someInt();
		int y2 = someInt();
		int z2 = someInt();
		Coordinate3D c2 = new Coordinate3D(x2, y2, z2);
		// When:
		Coordinate3D actual = c1.minus(c2);
		// Then:
		assertThat(actual.getX()).isEqualTo(x1 - x2);
		assertThat(actual.getY()).isEqualTo(y1 - y2);
		assertThat(actual.getZ()).isEqualTo(z1 - z2);
	}

}
