public class BurningShipGenerator implements FractalGenerator {
	@Override
	public int steps(MutableComplexDouble c) {
		int steps = 0;
		
		MutableComplexDouble z = new MutableComplexDouble(c);
		
		while (z.norm2() < 16.0 && steps < THRESHOLD_STEPS) {
			z.abs().sqrplusc(c);
			steps++;
		}
		
		return steps;
	}
}