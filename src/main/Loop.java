package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class Loop implements Runnable {


	public enum CHECK_TYPE {
		BRUTE, HASH;
	}

	public static final CHECK_TYPE TYPE = CHECK_TYPE.HASH;

	private Display display;
	public static int width, height;
	private String title;
	private boolean running = false;
	private Thread thread;

	private BufferStrategy bs;
	private Graphics2D g;

	private Input input = new Input();

	public long updateLength;

	//CONSTRUCTOR
	public Loop(String title, int width, int height){
		Loop.width = width;
		Loop.height = height;
		this.title = title;
	}

	//STARTS THREAD
	public synchronized void start(){

		if(running)
			return;

		running = true;
		thread = new Thread(this);
		thread.start();
	}

	//STOPS THREAD
	public synchronized void stop(){

		if(!running)
			return;

		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}



	public void run(){
		init();

		long lastLoopTime = System.nanoTime();
		final int TARGET_FPS = 600;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;   

		// keep looping round til the game ends
		while (running){
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long now = System.nanoTime();
			updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / ((double)OPTIMAL_TIME);


			// update the game logic
			tick(delta);

			// draw everyting
			render();

			try{
				Thread.sleep( (lastLoopTime - System.nanoTime() + OPTIMAL_TIME)/1000000 );
			}catch(Exception e){			};
		}
		stop();
	}

	//UPDATES VARIABLES
	private void tick(double delta){

		if(TYPE == TYPE.HASH) {
			Boid.BOID_HASH.clear();

			for(Boid b : Boid.ALL_BOIDS)
				Boid.BOID_HASH.put(b);

		}
		
		for(Boid b : Boid.ALL_BOIDS)
			b.tick(delta);


	}

	//RENDERS TO SCREEN
	private void render(){
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null){
			display.getCanvas().createBufferStrategy(2);
			return;
		}

		//draws to screen
		g = (Graphics2D) bs.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);

		for(Boid b : Boid.ALL_BOIDS)
			b.render(g);

		for(int i = 0; i < Block.ALL_BLOCKS.size(); i++) {
			Block b = Block.ALL_BLOCKS.get(i);
			b.render(g);
		}

		g.setColor(Color.WHITE);

		g.setFont(new Font("Comic Sans MS", Font.BOLD, 40));

		String fps = String.format("FPS: %.1f", (double)(1e9/updateLength));

		g.drawString(fps, 0, Loop.height);

		//ends the drawing
		bs.show();
		g.dispose();
	}



	private void init(){		
		display = new Display(title,width, height);
		display.getCanvas().addMouseMotionListener(input);
		display.getJFrame().addMouseMotionListener(input);
		display.getCanvas().addMouseListener(input);
		display.getJFrame().addMouseListener(input);

		Random r = new Random();

		for(int i = 0; i < 1000; i++){
			new Boid(r.nextInt(width), r.nextInt(height));
		}
	}
}