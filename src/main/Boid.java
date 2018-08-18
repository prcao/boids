package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import main.util.QuadTree;
import main.util.SpatialHashMap;
import main.util.Vector;

public class Boid extends Entity {

	public static final LinkedList<Boid> ALL_BOIDS = new LinkedList<Boid>();

	public static final Random RNG = new Random();

	private static final int SIZE = 15;
	private static final int DEFAULT_SPEED = 3;
	private static final double MAX_SPEED = SIZE / 6;
	private static final int NOISE = 3;
	private static final double DAMPING = .1;
	private static final double BLOCK_STRENGTH = 5;
	private static final double BLOCK_CLOSENESS_THRESHOLD = Block.SIZE * 2;

	private static final double TRIANGLE_BASE_ANGLE = Math.toRadians(30);

	private static final double NEIGHBOR_RADIUS_THRESHOLD = SIZE * 5;
	private static final double CLOSENESS_THRESHOLD = SIZE * 4;

	public static final SpatialHashMap<Boid> BOID_HASH = new SpatialHashMap<Boid>(Loop.width / 32);
	public static final QuadTree<Boid> BOID_TREE = new QuadTree<>(Loop.width, Loop.height);

	private Color color = new Color(RNG.nextFloat() / 2f + .5f, RNG.nextFloat() / 2f + .5f, RNG.nextFloat() / 2f + .5f);

	private static final double COMPONENT = NEIGHBOR_RADIUS_THRESHOLD * Math.cos(Math.PI / 4);
	private Vector vel;

	private ArrayList<Boid> neighbors = new ArrayList<Boid>();

	public Boid(int x, int y) {
		pos = new Vector(x, y);

		width = SIZE;
		height = SIZE;

		vel = new Vector(RNG.nextInt(DEFAULT_SPEED) - (DEFAULT_SPEED/2), RNG.nextInt(DEFAULT_SPEED) - (DEFAULT_SPEED/2));
		ALL_BOIDS.add(this);
	}

	public void render(Graphics2D g) {

		//generate triangle points
		Vector heading = vel.normalize();
		Vector left = heading.rotate(TRIANGLE_BASE_ANGLE);
		Vector right = heading.rotate(-TRIANGLE_BASE_ANGLE);

		//rotation
		int[] xPoints = { (int) (pos.x + SIZE * heading.x), (int) (pos.x - SIZE * left.x), (int) (pos.x - SIZE * right.x) };
		int[] yPoints = { (int) (pos.y + SIZE * heading.y), (int) (pos.y - SIZE * left.y), (int) (pos.y - SIZE * right.y) };

		g.setColor(color);
		g.fillPolygon(xPoints, yPoints, 3);
		//g.drawRect((int) (pos.x + SIZE *heading.x) - 2, (int) (pos.y + SIZE * heading.y) - 2, 4, 4);
	}

	public void tick(double delta) {

		neighbors = getNeighbors();

		Vector[] info = getInfo();

		for(Vector v : info) {
			vel.addToThis(v);
		}

		vel.limit(MAX_SPEED);

		pos.addToThis(vel);

		pos.x %= Loop.width;
		pos.y %= Loop.height;

		if(pos.x < 0)
			pos.x = Loop.width - 1;
		if(pos.y < 0)
			pos.y = Loop.height - 1;
	}

	private ArrayList<Boid> getNeighbors() {

		ArrayList<Boid> toRet = new ArrayList<Boid>();

		/* BRUTE FORCE */
		if(Loop.TYPE == Loop.CHECK_TYPE.BRUTE) {
			for(Boid b : ALL_BOIDS) {
				if(b == this) continue;

				if(distance(b) < NEIGHBOR_RADIUS_THRESHOLD) {
					toRet.add(b);
				}
			}
		}

		/* SPATIAL HASHING */
		else if(Loop.TYPE == Loop.CHECK_TYPE.HASH) {
			Set<ArrayList<Boid>> toProcess = new HashSet<>();

			LinkedList<ArrayList<Boid>> toAdd = new LinkedList<>();

			toProcess.add(BOID_HASH.get(pos));
			toProcess.add(BOID_HASH.get(pos.add(COMPONENT, COMPONENT)));
			toProcess.add(BOID_HASH.get(pos.add(COMPONENT, -COMPONENT)));
			toProcess.add(BOID_HASH.get(pos.add(-COMPONENT, COMPONENT)));
			toProcess.add(BOID_HASH.get(pos.add(-COMPONENT, -COMPONENT)));

			for(ArrayList<Boid> boids : toProcess) {

				for(Boid b : boids) {

					if(b == this) continue;

					if(distance(b) < NEIGHBOR_RADIUS_THRESHOLD) {
						toRet.add(b);
					}
				}
			}
		}
		
		/* QuadTree */
		else if(Loop.TYPE == Loop.CHECK_TYPE.QUAD) {
			Set<List<Boid>> toProcess = new HashSet<>();
			
			toProcess.add(BOID_TREE.get(pos));
			toProcess.add(BOID_TREE.get(pos.add(COMPONENT, COMPONENT)));
			toProcess.add(BOID_TREE.get(pos.add(COMPONENT, -COMPONENT)));
			toProcess.add(BOID_TREE.get(pos.add(-COMPONENT, COMPONENT)));
			toProcess.add(BOID_TREE.get(pos.add(-COMPONENT, -COMPONENT)));
			
			for(List<Boid> boids : toProcess) {

				for(Boid b : boids) {

					if(b == this) continue;

					if(distance(b) < NEIGHBOR_RADIUS_THRESHOLD) {
						toRet.add(b);
					}
				}
			}
		}
		
		return toRet;
	}

	//returns information about neighbor boids so that only 1 pass need s to be made
	private Vector[] getInfo() {

		Vector[] toRet = { new Vector(), new Vector(), new Vector(), new Vector() };

		for(Boid b : neighbors) {

			if(b == this) continue;

			Vector s = displacement(b);
			double distance = s.magnitude();

			if(distance < CLOSENESS_THRESHOLD)
				toRet[hashAttr("seperation")].addToThis(s.mult(-1 / (distance)));

			toRet[hashAttr("cohesion")].addToThis(displacement(b));
			toRet[hashAttr("alignment")].addToThis(b.vel);
		}

		for(int i = 0; i < Block.ALL_BLOCKS.size(); i++) {

			Vector s = displacement(Block.ALL_BLOCKS.get(i));
			double distance = s.magnitude();

			if(distance < BLOCK_CLOSENESS_THRESHOLD) {
				toRet[hashAttr("blocking")].addToThis(s.mult(-1.0 / (distance)));
			}
		}

		for(Vector v : toRet) {
			v.normalizeThis();
			//v.mult(1.0 / neighbors.size());
			v.multThis(DAMPING);
		}

		//modifiers
		toRet[hashAttr("blocking")].multThis(BLOCK_STRENGTH);
		//	toRet[hashAttr("seperation")].multThis(neighbors.size());

		toRet[hashAttr("seperation")].multThis(1.1);
		toRet[hashAttr("alignment")].multThis(1);
		toRet[hashAttr("cohesion")].multThis(1);

		return toRet;
	}

	//fake hashing
	private int hashAttr(String attr) {

		switch(attr) {
		case "seperation":
			return 0;
		case "alignment":
			return 1;
		case "cohesion":
			return 2;
		case "blocking":
			return 3;
		default:
			throw new IllegalArgumentException();
		}
	}

}