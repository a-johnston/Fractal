public class JuliaGenerator implements FractalGenerator {
	private static final int THRESHOLD_STEPS = 100;
	
	private final MutableComplexDouble c;
	
	public JuliaGenerator(MutableComplexDouble c) {
		this.c = c;
	}
	
	@Override
	public int steps(MutableComplexDouble z) {
		int steps = 0;
		
		while (z.norm2() < 4.0 && steps < THRESHOLD_STEPS) {
			z.sqrplusc(c);
			steps++;
		}
		
		return steps;
	}
}