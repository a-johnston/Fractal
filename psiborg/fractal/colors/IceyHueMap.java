package psiborg.fractal.colors;

public class IceyHueMap implements ColorMap {
	private int[][] colors;
	
	@Override
	public void generate(int n) {
		colors = new int[n][3];
		
		double h;
		int x;
		for (int i = 0; i < n; i++) {
			h = i * 6.0 / n;
			x = (int) (255 * Math.abs(h % 2 - 1.0));
			colors[i][0] = 0;
			colors[i][1] = x;
			colors[i][2] = 255;
		}
	}

	@Override
	public int[] get(int n) {
		if (n == -1) {
			return BLACK;
		}
		return colors[n % colors.length];
	}

}
