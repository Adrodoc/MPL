package de.adrodoc55.minecraft.mpl;

public class Scribble {

	private static final int VARIANTS = 5;

	private static final int SIZE = 3;

	public static void main(String[] args) {
		int[][][] matrix3d = new int[SIZE][SIZE][SIZE];

		try {
			int x = 0;
			int y = 0;
			int z = 0;
			for (int a = 0; /* x != SIZE && y != SIZE && z != SIZE */; a++) {
				matrix3d[z][y][x] = matrix3d[z][y][x] + 1;
				System.out.println("hi");
				switch (a % VARIANTS) {
				case 0:
					x++;
					break;
				case 1:
					y++;
					break;
				case 2:
					x--;
					break;
				case 3:
					z++;
					break;
				case 4:
					y--;
					break;
				default:
					break;
				}

			}
		} catch (Exception ex) {

		}
		for (int z = 0; z < matrix3d.length; z++) {
			int[][] mz = matrix3d[z];
			for (int y = 0; y < mz.length; y++) {
				int[] my = mz[y];
				for (int x = 0; x < my.length; x++) {
					int mx = my[z];
					System.out.print(mx + "\t");
				}
				System.out.println();
			}
			System.out.println();
		}

	}

}
