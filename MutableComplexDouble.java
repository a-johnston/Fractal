
public class MutableComplexDouble {
	public double x;
	public double y;
	private double t;
	
	public MutableComplexDouble(double x, double y) {
		set(x, y);
	}
	
	public MutableComplexDouble(MutableComplexDouble source) {
		set(source.x, source.y);
	}
	
	public MutableComplexDouble set(MutableComplexDouble source) {
		this.x = source.x;
		this.y = source.y;
		return this;
	}
	
	public MutableComplexDouble set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public MutableComplexDouble add(MutableComplexDouble other) {
		x += other.x;
		y += other.y;
		return this;
	}
	
	public MutableComplexDouble mult(MutableComplexDouble other) {
		t = x;
		other.t = other.x;
		x = t * other.x - y * other.y;
		y = t * other.y + y * other.t;
		return this;
	}
	
	public MutableComplexDouble sqrplusc(MutableComplexDouble c) {
		t = x;
		
		x = x * x - y * y + c.x;
		y = t * y + y * t + c.y;
		
		return this;
	}
	
	public MutableComplexDouble abs() {
		x = Math.abs(x);
		y = Math.abs(y);
		return this;
	}
	
	public double norm2() {
		return x*x + y*y;
	}
	
	public double norm() {
		return Math.sqrt(x*x + y*y);
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode(x) ^ Double.hashCode(y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MutableComplexDouble)) {
			return false;
		}
		MutableComplexDouble other = (MutableComplexDouble) obj;
		return x == other.x && y == other.y;
	}
	
}
