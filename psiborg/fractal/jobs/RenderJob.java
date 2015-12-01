package psiborg.fractal.jobs;

import psiborg.fractal.Viewport;

public class RenderJob {
	private static long jobId = 0L;
	private static long oldId = -1L;
	
	public final long id;
	public final Viewport segment;
	public final Viewport view;
	
	private final Runnable callback;
	
	public RenderJob(Viewport segment, Viewport view, Runnable callback) {
		this.id = getId();
		this.segment = segment;
		this.view = view;
		this.callback = callback;
	}
	
	public boolean isActive() {
		return id > oldId;
	}
	
	public void finish() {
		if (callback != null) {
			callback.run();
		}
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
