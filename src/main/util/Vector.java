package main.util;

public class Vector {

	public double x, y;

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector() {
		this(0, 0);
	}

	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y);
	}
	
	public Vector add(double x, double y) {
		return new Vector(this.x + x, this.y + y);
	}

	public void addToThis(Vector v) {
		x += v.x;
		y += v.y;
	}

	public void addToThis(double x, double y) {
		this.x += x;
		this.y += y;
	}

	public Vector mult(double scalar) { 
		return new Vector(scalar * x, scalar * y);
	}

	public void multThis(double scalar) { 
		x *= scalar;
		y *= scalar;
	}
	
	public Vector pow(double scalar) {
		return new Vector(Math.pow(x, scalar), Math.pow(y, scalar));
	}

	public void subtract(Vector v) {
		x -= v.x;
		y -= v.y;
	}

	public void subractToThis(double x, double y) {
		this.x -= x;
		this.y -= y;
	}

	public double magnitude() {
		return Math.sqrt((x*x) + (y*y));
	}

	public Vector normalize() {
		double magnitude = Math.sqrt((x*x) + (y*y));

		return new Vector(x/magnitude, y/magnitude);
	}

	public void normalizeThis() {
		
		if(x == 0 && y == 0) return;
		
		double magnitude = Math.sqrt((x*x) + (y*y));

		x /= magnitude;
		y /= magnitude;
	}

	public void limit(double magnitude) {

		if(magnitude() > magnitude) {		
			normalizeThis();
			multThis(magnitude);
		}
	}

	public Vector rotate(double radAngle) {

		double sin = Math.sin(radAngle);
		double cos = Math.cos(radAngle);

		return new Vector(x * cos - y * sin, sin * x + cos * y);

	}

	public String toString() {
		return "<" + x + ", " + y + ">";
	}
}
