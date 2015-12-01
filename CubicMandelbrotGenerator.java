public class CubicMandelbrotGenerator implements FractalGenerator {
	@Override
	public int steps(MutableComplexDouble c) {
		int steps = 0;
		
		MutableComplexDouble z = new MutableComplexDouble(c);
		MutableComplexDouble t = new MutableComplexDouble(z);
		
		while (z.norm2() < 16.0 && steps < THRESHOLD_STEPS) {
			t.set(z);
			z.mult(t.mult(z)).add(c);
			steps++;
		}
		
		return steps;
	}
}