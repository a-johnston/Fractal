public class Runner {
	public static void main(String[] args) {
//		new ComplexPointDisplay(new JuliaGenerator(new MutableComplexDouble(.28, .008)));
		new ComplexPointDisplay(new MandelbrotGenerator(), new HueMap());
	}
}
