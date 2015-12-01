public class QuadMandelbrotGenerator implements FractalGenerator {
	@Override
	public int steps(MutableComplexDouble c) {
		int steps = 0;
		
		MutableComplexDouble z = new MutableComplexDouble(c);
		
		while (z.norm2() < 16.0 && steps < THRESHOLD_STEPS) {
			z.mult(z.mult(z)).add(c);
			steps++;
		}
		
		return steps;
	}
}