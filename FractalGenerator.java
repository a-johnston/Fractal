public interface FractalGenerator {
	public static final int THRESHOLD_STEPS = 100000;
	
	int steps(MutableComplexDouble c);
}
