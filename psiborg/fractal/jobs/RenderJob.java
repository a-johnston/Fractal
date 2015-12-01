package psiborg.fractal.jobs;

import psiborg.fractal.Viewport;

public class RenderJob {
	private static long jobId = 0L;
	private static long oldId = -1L;
	
	public final long id;
	public final Viewport segment;
	public final Viewport view;
	
	public RenderJob(Viewport segment, Viewport view) {
		this.id = getId();
		this.segment = segment;
		this.view = view;
	}
	
	public boolean isActive() {
		return id > oldId;
	}
	
	public static synchronized long getLastId() {
		return jobId;
	}
	
	public static synchronized void quitActive() {
		oldId = jobId-1;
	}
	
	private static synchronized long getId() {
		return jobId++;
	}
}
