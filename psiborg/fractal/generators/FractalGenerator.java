package psiborg.fractal.generators;

import psiborg.fractal.MutableComplexDouble;

public interface FractalGenerator {
	public static final int THRESHOLD_STEPS = 55000;
	
	int steps(MutableComplexDouble c);
}
