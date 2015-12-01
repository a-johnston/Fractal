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
	
	private int complete;
	
	private MutableComplexDouble value;
	private FractalGenerator fractal;
	private ColorMap colors;
	
	private Runnable callback;
	
	public FractalWorker(FractalGenerator fractal, ColorMap colors, BufferedImage image, Runnable callback) {
		this.raster = image.getRaster();
		this.value = new MutableComplexDouble(0.0, 0.0);
		this.fractal = fractal;
		this.colors = colors;
		this.callback = callback;
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
						x = (int) job.segment.w;
						y = (int) job.segment.h;
						break;
					}
					
					value.set(px, getY((int) job.segment.y + y));
					int steps = fractal.steps(value);
					try {
						raster.setPixel((int) (job.segment.x + x), (int) (job.segment.y + y), colors.get(steps));
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println(job.segment);
					}
				}
				complete = x + 1;
			}
			
			job.finish();
			dirty = false;
		}
	}
	
	public void split() {
		if (job == null) {
			return;
		}
		
		double mid = Math.round(((double) complete / job.segment.w + 1.0) / 2.0);
		JobQueue.addJob(new RenderJob(job.segment.subview(mid, 1.0), job.view, callback));
		this.job = new RenderJob(job.segment.subview(0.0, mid), job.view, callback);
	}
	
	public double left() {
		return job.segment.w - complete;
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
