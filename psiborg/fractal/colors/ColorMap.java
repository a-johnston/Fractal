package psiborg.fractal.colors;

public interface ColorMap {
	public static final int[] BLACK = {0, 0, 0};
	
	void generate(int n);
	
	int[] get(int n);
}
