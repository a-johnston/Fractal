package psiborg.fractal.jobs;

import psiborg.fractal.Viewport;

public class RenderJob {
	private static long oldId = -1L;
	
	public final long id;
	public final Viewport segment;
	public final Viewport view;
	
	private final Runnable callback;
	
	public RenderJob(Viewport segment, Viewport view, Runnable callback, RenderJob parent) {
		this.id = parent == null ? getId() : parent.id;
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
	
	public static synchronized void quitActive() {
		oldId = System.currentTimeMillis() - 1;
	}
	
	private static synchronized long getId() {
		return System.currentTimeMillis();
	}
}
