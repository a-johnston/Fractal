package psiborg.fractal.jobs;

import java.awt.image.Raster;

import psiborg.fractal.Viewport;

public class JobFactory {
	public static void chunk(Raster target, Viewport view, int times) {
		for (Viewport fragment : new Viewport(0, 0, target.getWidth(), target.getHeight()).tesselate(times)) {
			JobQueue.addJob(new RenderJob(fragment, view));
		}
	}
}
