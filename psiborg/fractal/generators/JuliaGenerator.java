package psiborg.fractal.generators;

import psiborg.fractal.MutableComplexDouble;

public class JuliaGenerator implements FractalGenerator {
	
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