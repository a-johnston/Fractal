package psiborg.fractal;

import java.util.ArrayList;
import java.util.List;

public class Viewport {
	private double x, y, w, h;

	public Viewport(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public List<Viewport> tesselate(int depth) {
		List<Viewport> l = new ArrayList<>(2 << depth);

		if (depth == 0) {
			l.add(this);
		} else {
			double mw = w / 2.0;
			double mh = h / 2.0;
			depth--;
			
			l.addAll(new Viewport(x, y, mw, mh).tesselate(depth));
			l.addAll(new Viewport(x, y + mh, mw, mh).tesselate(depth));
			l.addAll(new Viewport(x + mw, y, mw, mh).tesselate(depth));
			l.addAll(new Viewport(x + mw, y + mh, mw, mh).tesselate(depth));
		}
		return l;
	}
}
