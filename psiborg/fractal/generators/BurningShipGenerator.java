package psiborg.fractal.generators;

import psiborg.fractal.MutableComplexDouble;

public class BurningShipGenerator implements FractalGenerator {
	@Override
	public int steps(MutableComplexDouble c) {
		int steps = 0;
		
		MutableComplexDouble z = new MutableComplexDouble(c);
		
		while (steps < THRESHOLD_STEPS) {
			if (z.norm2() > 16.0) {
				return steps;
			}
			
			z.abs().sqrplusc(c);
			steps++;
		}
		
		return -1;
	}
}