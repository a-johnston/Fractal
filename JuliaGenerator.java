public class JuliaGenerator implements FractalGenerator {
	private static final int THRESHOLD_STEPS = 100;
	
	private final MutableComplexDouble c;
	
	public JuliaGenerator(MutableComplexDouble c) {
		this.c = c;
	}
	
	@Override
	public int steps(MutableComplexDouble z) {
		int steps = 0;
		
		while (steps < THRESHOLD_STEPS) {
			if (z.norm2() > 4.0) {
				return steps;
			}
			
			z.sqrplusc(c);
			steps++;
		}
		
		return -1;
	}
}