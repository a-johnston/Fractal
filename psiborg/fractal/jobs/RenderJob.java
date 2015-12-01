package psiborg.fractal.jobs;

import psiborg.fractal.Viewport;

public class RenderJob {
	private static long jobId = 0L;
	private static long oldId = -1L;
	
	public final long id;
	public final Viewport view;
	public final Viewport segment;
	
	public RenderJob(Viewport view, Viewport segment) {
		this.id = getId();
		this.view = view;
		this.segment = segment;
	}
	
	public boolean isActive() {
		return id > oldId;
	}
	
	public static synchronized long getLastId() {
		return jobId;
	}
	
	public static synchronized void quitActive() {
		oldId = jobId;
	}
	
	private static synchronized long getId() {
		return jobId++;
	}
}
