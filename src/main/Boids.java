package main;

public class Boids {

	public static int width = 2560, height = 1440;
	
	public static void main (String[] args){
		Loop loop = new Loop("Boids", width, height);
		loop.start();
	}

	
}


