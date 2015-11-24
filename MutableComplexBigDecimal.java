import java.math.BigDecimal;


public class MutableComplexBigDecimal {
	public BigDecimal x;
	public BigDecimal y;
	
	public MutableComplexBigDecimal(double x, double y) {
		set(x, y);
	}
	
	public MutableComplexBigDecimal(MutableComplexBigDecimal source) {
		set(source.x, source.y);
	}
	
	public MutableComplexBigDecimal set(double x, double y) {
		this.x = new BigDecimal(x);
		this.y = new BigDecimal(y);
		return this;
	}
	
	public MutableComplexBigDecimal set(BigDecimal x, BigDecimal y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public MutableComplexBigDecimal add(MutableComplexBigDecimal other) {
		this.x = this.x.add(other.x);
		this.y = this.y.add(other.y);
		return this;
	}
	
	public MutableComplexBigDecimal mult(MutableComplexBigDecimal other) {
		BigDecimal t = this.x;
		this.x = t.multiply(other.x).subtract(this.y.multiply(other.y));
		this.y = t.multiply(other.y).subtract(this.y.multiply(other.x));
		return this;
	}
	
	public double norm() {
		return Math.sqrt(x.pow(2).add(y.pow(2)).doubleValue());
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
	
	@Override
	public int hashCode() {
		return x.hashCode() ^ y.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MutableComplexDouble)) {
			return false;
		}
		MutableComplexDouble other = (MutableComplexDouble) obj;
		return this.x.equals(other.x) && this.y.equals(other.y);
	}
	
}
