package psiborg.fractal;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import psiborg.fractal.colors.ColorMap;
import psiborg.fractal.colors.HueMap;
import psiborg.fractal.generators.FractalGenerator;
import psiborg.fractal.generators.MandelbrotGenerator;
import psiborg.fractal.jobs.JobFactory;
import psiborg.fractal.jobs.JobQueue;
import psiborg.fractal.jobs.RenderJob;

public class ComplexImage implements Runnable {
	private final int SUPERSAMPLE;
	private final int NUM_WORKERS;

	private boolean ready;
	private boolean dirty;
	private BufferedImage raw;
	private BufferedImage image;
	
	private FractalWorker[] workers;
	private ColorMap map;

	private Benchmark bench;

	public ComplexImage(int width, int height) {
		this(width, height, new MandelbrotGenerator());
	}

	public ComplexImage(int width, int height, FractalGenerator fractal) {
		this(width, height, fractal, new HueMap());
	}

	public ComplexImage(int width, int height, FractalGenerator fractal, ColorMap colors) {
		this(width, height, fractal, colors, 8, 1);
	}

	public ComplexImage(int width, int height, FractalGenerator fractal, ColorMap colors, int workers, int supersample) {
		this.NUM_WORKERS = workers;
		this.SUPERSAMPLE = supersample;

		image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		raw = new BufferedImage(width * SUPERSAMPLE, height * SUPERSAMPLE, BufferedImage.TYPE_3BYTE_BGR);

		this.map = colors;
		this.map.generate(500);

		this.workers = new FractalWorker[NUM_WORKERS];

		for (int n = 0; n < NUM_WORKERS; n++) {
			this.workers[n] = new FractalWorker(fractal, colors, raw, this);
			this.workers[n].start();
		}

		bench = new Benchmark("Frame complete");
	}

	public BufferedImage getImage() {
		if (!ready) {
			Graphics2D g2d = (Graphics2D) image.getGraphics();

			if (SUPERSAMPLE > 1) {
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			}

			g2d.drawImage(raw, 0, 0, image.getWidth(), image.getHeight(), null);
			
			ready = !dirty;
		}
		
		return image;
	}
	
	public boolean isDirty() {
		return dirty;
	}

	private FractalWorker getSlowestWorker() {
		FractalWorker slow = null;
		double left = 10;
		double t;

		for (FractalWorker worker : workers) {
			t = worker.left();
			if (t > left) {
				left = t;
				slow = worker;
			}
		}

		return slow;
	}

	public void startDraw(Viewport view) {
		bench.start();
		ready = false;
		dirty = true;

		RenderJob.quitActive();
		JobFactory.chunk(raw.getRaster(), view, 2, this);
	}

	@Override
	public void run() {
		if (JobQueue.isEmpty()) {
			boolean dirty = false;
			boolean free = false;
			for (FractalWorker worker : workers) {
				dirty = dirty || worker.isDirty();
				free = free || !worker.isDirty();
			}

			if (dirty) {
				if (free) {
					FractalWorker w = getSlowestWorker();

					if (w != null) {
						w.split();
					}
				}
			} else {
				this.dirty = false;
				bench.stop();
			}
		}
	}
}
