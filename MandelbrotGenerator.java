public class MandelbrotGenerator implements FractalGenerator {
	@Override
	public int steps(MutableComplexDouble c) {
		int steps = 0;
		
		MutableComplexDouble z = new MutableComplexDouble(c);
		
		while (z.norm2() < 4.0 && steps < THRESHOLD_STEPS) {
			z.sqrplusc(c);
			steps++;
		}
		
		return steps;
	}
}