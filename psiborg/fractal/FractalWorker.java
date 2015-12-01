package psiborg.fractal;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import psiborg.fractal.colors.ColorMap;
import psiborg.fractal.generators.FractalGenerator;
import psiborg.fractal.jobs.JobQueue;
import psiborg.fractal.jobs.RenderJob;

public class FractalWorker extends Thread {
	private WritableRaster raster;
	
	private RenderJob job;
	private boolean dirty;
	
	private MutableComplexDouble value;
	private FractalGenerator fractal;
	private ColorMap colors;
	
	public FractalWorker(FractalGenerator fractal, ColorMap colors, BufferedImage image) {
		this.raster = image.getRaster();
		this.value = new MutableComplexDouble(0.0, 0.0);
		this.fractal = fractal;
		this.colors = colors;
	}
	
	@Override
	public void run() {
		int x, y;
		double px;
		
		while (true) {
			job = JobQueue.getJob();
			dirty = true;
			
			for (x = 0; x < job.segment.w; x += 1) {
				px = getX((int) job.segment.x + x);
				for (y = 0; y < job.segment.h; y += 1) {
					if (!job.isActive()) {
						break;
					}
					
					value.set(px, getY((int) job.segment.y + y));
					int steps = fractal.steps(value);
					raster.setPixel((int) (job.segment.x + x % raster.getWidth()), (int) (job.segment.y + y % raster.getHeight()), colors.get(steps));
				}
			}
			
			dirty = false;
		}
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	private synchronized double getX(int x) {
		return (x / (double) raster.getWidth()) * job.view.w + job.view.x;
	}

	private synchronized double getY(int y) {
		return -job.view.y - (y / (double) raster.getHeight() * job.view.h);
	}
}
