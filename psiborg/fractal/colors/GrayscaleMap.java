package psiborg.fractal.colors;

public class GrayscaleMap implements ColorMap {

	private int[][] colors;
	
	@Override
	public void generate(int n) {
		colors = new int[n][3];
		
		int t;
		for (int i = 0; i < n; i++) {
			t = i % 255;
			colors[i][0] = t;
			colors[i][1] = t;
			colors[i][2] = t;
		}
	}

	@Override
	public int[] get(int n) {
		return colors[n % colors.length];
	}
}
