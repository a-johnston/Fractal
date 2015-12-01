public class HueMap implements ColorMap {
	private int[][] colors;
	
	@Override
	public void generate(int n) {
		colors = new int[n][3];
		
		double h;
		int x, ih;
		for (int i = 0; i < n; i++) {
			h = i * 6.0 / n;
			x = (int) (255 * (1 - Math.abs((h % 2) - 1)));
			ih = (int) h;
			
			colors[i][0] = (ih == 0 || ih == 5) ? 255 : ((ih == 1 || ih == 4) ? x : 0); //r
			colors[i][1] = (ih == 1 || ih == 2) ? 255 : ((ih == 0 || ih == 3) ? x : 0); //g
			colors[i][2] = (ih == 3 || ih == 4) ? 255 : ((ih == 2 || ih == 5) ? x : 0); //b
		}
	}
	
	@Override
	public int[] get(int n) {
		return colors[n % colors.length];
	}

}
