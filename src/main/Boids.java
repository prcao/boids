package main;

public class Boids {

	public static int width = 1920, height = 1080;
	
	public static void main (String[] args){
		Loop loop = new Loop("Boids", width, height);
		loop.start();
	}
}


