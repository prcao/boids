package main;

import main.util.Vector;

public abstract class Entity {
	
	public Vector pos;
	public int width, height;
	
	protected double distance(Entity b) {
		return Math.abs(Math.hypot(pos.x - b.pos.x, pos.y - b.pos.y));
	}
	
	protected Vector displacement(Entity b) {
		return new Vector(b.pos.x - pos.x, b.pos.y - pos.y);
	}
}
