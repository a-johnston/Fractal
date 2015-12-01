package psiborg.fractal;

import psiborg.fractal.colors.HueMap;
import psiborg.fractal.generators.MandelbrotGenerator;

public class Runner {
	public static void main(String[] args) {
		new ComplexPointDisplay(new MandelbrotGenerator(), new HueMap());
	}
}
