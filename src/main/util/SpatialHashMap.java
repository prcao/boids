package main.util;

import java.util.ArrayList;

import main.Entity;
import main.Loop;

/*
 * Grid-based spatial data structure intended for collision detection
 */
public class SpatialHashMap<E extends Entity> {

	private final int BLOCK_WIDTH;
	private final int BLOCK_HEIGHT;
	private ArrayList<E>[][] grid;
	
	public SpatialHashMap(int gridWidth, int gridHeight, int blockWidth, int blockHeight) {
		grid = (ArrayList<E>[][])new ArrayList[gridHeight][gridWidth];
		BLOCK_WIDTH = blockWidth;
		BLOCK_HEIGHT = blockHeight;
		
		System.out.println("Created " + gridWidth + "," + gridHeight + " Matrix for " + blockWidth + " size");
		
		//initialize all buckets
		for(int row = 0; row < grid.length; row++) {
			for(int col = 0; col < grid[row].length; col++)
				grid[row][col] = new ArrayList<E>();
		}
	}
	
	public SpatialHashMap(int blockSize) {
		this(Loop.width / blockSize + 1, Loop.height / blockSize + 1, blockSize, blockSize);
	}
	
	public SpatialHashMap(int gridSize, int blockWidth, int blockHeight) {
		this(gridSize, gridSize, blockWidth, blockHeight);
	}
	
	//retrieve all entities that might collide from given position
	public ArrayList<E> get(Vector pos) {
		return get(pos.x, pos.y);
	}
	
	//get all entities in that grid
	public ArrayList<E> get(double x, double y) {
		return grid[hashY(y)][hashX(x)];
	}
	
	//adds an entity to its respective buckets
	public void put(E data) {
		
		int minX = hashX(data.pos.x);
		int minY = hashY(data.pos.y);
		
		int maxX = hashX(data.pos.x + data.width);
		int maxY = hashY(data.pos.y + data.height);
		
		//add entity to all intersecting buckets
		for(int row = minY; row <= maxY; row++) {
			for(int col = minX; col <= maxX; col++) {
				grid[row][col].add(data);
			}
		}
	}
	
	public int hashX(double x) {
		return (int)(x / BLOCK_WIDTH);
	}
	
	public int hashY(double y) {
		return (int)(y / BLOCK_HEIGHT);
	}
	
	//clears all buckets
	public void clear() {
		for(int row = 0; row < grid.length; row++) {
			for(int col = 0; col < grid[row].length; col++)
				grid[row][col].clear();
		}
	}
}
