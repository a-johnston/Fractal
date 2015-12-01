package psiborg.fractal;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import psiborg.fractal.colors.ColorMap;
import psiborg.fractal.generators.FractalGenerator;

public class FractalWorker extends Thread {
	private BufferedImage image;
	private WritableRaster raster;
	
	private boolean dirty;
	private boolean restart;
	
	private double viewX;
	private double viewY;
	private double viewW;
	private double viewH;
	
	private MutableComplexDouble value;
	private FractalGenerator fractal;
	private ColorMap colors;
	
	public FractalWorker(FractalGenerator fractal, ColorMap colors, BufferedImage image) {
		this.image = image;
		this.raster = image.getRaster();
		this.value = new MutableComplexDouble(0.0, 0.0);
		this.fractal = fractal;
		this.colors = colors;
	}
	
	public synchronized void setView(double x, double y, double w, double h) {
		viewX = x;
		viewY = y;
		viewW = w;
		viewH = h;
		
		if (!dirty) {
			dirty = true;
			notify();
		} else {
			restart = true;
		}
	}
	
	public boolean isDirty() {
		synchronized (this) {			
			return dirty;
		}
	}
	
	@Override
	public void run() {
		while (true) {
			while (!dirty) {
				synchronized (this) {				
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			int x, y;
			double px;
			for (x = 0; x < image.getWidth(); x += 1) {
				px = getX(x);
				for (y = 0; y < image.getHeight(); y += 1) {
					if (restart) {
						x = 0;
						y = 0;
						restart = false;
						continue;
					}
					
					value.set(px, getY(y));
					int steps = fractal.steps(value);
					raster.setPixel(x, y, colors.get(steps));	
				}
			}
			synchronized (this) {				
				dirty = false;
			}
		}
	}
	
	private synchronized double getX(int x) {
		return (x / (double) image.getWidth()) * viewW + viewX;
	}

	private synchronized double getY(int y) {
		return -viewY - (y / (double) image.getHeight() * viewH);
	}
}
