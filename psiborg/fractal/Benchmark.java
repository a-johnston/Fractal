package psiborg.fractal;

public class Benchmark {
	private String name;
	private boolean started;
	private long start;
	
	public Benchmark() {
		this("Benchmark");
	}
	
	public Benchmark(String name) {
		this.name = name;
	}
	
	public void start() {
		started = true;
		start = System.currentTimeMillis();
	}
	
	public void stop() {
		if (started) {
			System.out.println(name + " : " + (Math.round((System.currentTimeMillis() - start) / 10.0) / 100.0) + "s");
			started = false;
		}
	}
}
