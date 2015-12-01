public class MandelbrotGenerator implements FractalGenerator {
	@Override
	public int steps(MutableComplexDouble c) {
		int steps = 0;
		
		MutableComplexDouble z = new MutableComplexDouble(c);
		
		double normie = 0.0;
		
		while (normie < 16.0 && steps < THRESHOLD_STEPS) {
			z.sqrplusc(c);
			steps++;
			
			normie = z.norm2();
			
//			if (normie < 0.01) {
//				return THRESHOLD_STEPS;
//			}
		}
		
		return steps;
	}
}