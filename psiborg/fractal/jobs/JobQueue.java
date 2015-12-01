package psiborg.fractal.jobs;

import java.util.LinkedList;
import java.util.Queue;

public class JobQueue {
	private static Queue<RenderJob> q = new LinkedList<>();
	
	public static synchronized void addJob(RenderJob job) {
		q.add(job);
		JobQueue.class.notify();
	}
	
	public static synchronized RenderJob getJob() {
		while (q.isEmpty()) {
			try {
				JobQueue.class.wait();
			} catch (InterruptedException ignored) {}
		}
		return q.remove();
	}
	
	public static synchronized boolean isEmpty() {
		return q.isEmpty();
	}
}
