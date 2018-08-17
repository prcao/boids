package main.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import main.Entity;

/*
 * Quaternary Tree 
 * Spatial tree to check proximity
 */
public class QuadTree<E extends Entity> {

	private final int MAX_ENTITIES;
	private static final int MAX_LEVELS = 5;
	private Node root;
	
	//default -> maxEntities = 5
	public QuadTree(int width, int height) {
		this(width, height, 5);
	}
	
	public QuadTree(int width, int height, int maxEntities) {
		root = new Node(1, 0, 0, width, height);
		
		MAX_ENTITIES = maxEntities;
	}

	//inserts an entity into the quadtree
	public void insert(E toInsert) {
		insert(toInsert, root);
	}

	//helper method for insert
	private void insert(E data, Node current) {

		//split quadrant
		if(current.entities.size() > MAX_ENTITIES && current.level < MAX_LEVELS) {
			current.split();			
		}

		//insert the data into subquadrant(s)
		if(current.quads[0] != null) {

			ArrayList<Integer> occupiedQuads = current.determineQuadrants(data);
			
			for(int quad : occupiedQuads) {
				insert(data, (Node)current.quads[quad]);
			}
		}

		else {
			current.entities.add(data);
		}
	}

	//deletes all entities from the quadtree
	public void clear() {
		clear(root);
	}

	//helper for deleting all entities from quadtree
	private void clear(Node current) {

		if(current == null)
			return;

		for(int i = 0; i < current.entities.size(); i++) {
			clear((Node)current.quads[i]);
			current.quads[i] = null;
		}
	}

	//gets a list of other entities that this one could collide with
	public List<E> get(Entity entity) {
		return get(entity, new ArrayList<E>(), root);
	}
	
	public List<E> get(Vector pos) {
		return get(pos, new ArrayList<E>(), root);
	}
	
	private List<E> get(Vector pos, List<E> toReturn, Node current) {
		if(current.quads[0] == null) {
			return current.entities;
		}
		
		return get(pos, toReturn, (Node)current.quads[current.determineQuadrant(pos)]);
	}

	private List<E> get(Entity entity, List<E> toReturn, Node current) {

		if(current.quads[0] == null) {
			return current.entities;
		}

		return get(entity, toReturn, (Node)current.quads[current.determineQuadrant(entity.pos)]);
	}

	/*
	 * Node for quadtree
	 */
	private class Node {

		/*
		 * Index 0 -> Top Right (quad 1)
		 * Index 1 -> Top Left (quad 2)
		 * Index 2 -> Bottom Left (quad 3)
		 * Index 3 -> Bottom right (quad 4)
		 * 
		 * Array of objects because Java won't let me use an array of Nodes
		 * Node[] quads = new Node[4]; is disallowed
		 */
		private Object[] quads = new Object[4];

		private ArrayList<E> entities = new ArrayList<>(MAX_ENTITIES); 
		private Rectangle bounds;

		private int level;

		private Node(int level, int x, int y, int width, int height) {
			this.level = level;
			this.bounds = new Rectangle(x, y, width, height);
		}

		//split node into four and reorganize entities within
		private void split() {
			
			int subWidth = bounds.width / 2;
			int subHeight = bounds.height / 2;
			int x = bounds.x;
			int y = bounds.y;

			quads[0] = new Node(level + 1, x + subWidth, y, subWidth, subHeight);
			quads[1] = new Node(level + 1, x, y, subWidth, subHeight);
			quads[2] = new Node(level + 1, x, y + subHeight, subWidth, subHeight);
			quads[3] = new Node(level + 1, x + subWidth, y + subHeight, subWidth, subHeight);

			for(E e : entities) {

				ArrayList<Integer> occupiedQuads = determineQuadrants(e);

				for(int quad : occupiedQuads)
					insert(e, (Node)quads[quad]);
			}
			
			entities.clear();
		}
		
		public String toString() {
			return bounds.toString().substring(18);
		}

		private ArrayList<Integer> determineQuadrants(Entity data) {

			ArrayList<Integer> toReturn = new ArrayList<Integer>(4);

			//set to keep track of which quads were already put in the array
			boolean[] isInQuad = new boolean[4];

			/*
			 * Index 0 = quad 1
			 * Index 2 = quad 2
			 * etc.
			 */
			Vector[] corners =  {
					new Vector(data.pos.x + data.width, data.pos.y),
					data.pos,
					new Vector(data.pos.x, data.pos.y + data.height),
					new Vector(data.pos.x + data.width, data.pos.y + data.height)
			};			

			for(int i = 0; i < 4; i++) {
				int quadrant = determineQuadrant(corners[i]);

				if(!isInQuad[quadrant])
					toReturn.add(quadrant);

				isInQuad[quadrant] = true;
			}

			return toReturn;
		}

		//determines quadrant the coord should be put in
		private int determineQuadrant(Vector pos) {
			return determineQuadrant(pos.x, pos.y);
		}

		private int determineQuadrant(double x, double y) {

			final int VERT_AXIS = (bounds.width / 2) + bounds.x;
			final int HORIZ_AXIS = (bounds.height / 2) + bounds.y;

			if(y < HORIZ_AXIS) {
				if(x > VERT_AXIS)
					return 0;

				return 1;
			}

			if(x > VERT_AXIS)
				return 3;

			return 2;
		}
	}
}
