package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import main.util.Vector;

public class Block extends Entity implements MouseListener {
	
	
	public static final ArrayList<Block> ALL_BLOCKS = new ArrayList<Block>();
	public static final int SIZE = 20;
	
	public Block(int x, int y) {
		pos = new Vector(x, y);
		
		width = SIZE;
		height = SIZE;
		
		ALL_BLOCKS.add(this);
	}
	
	public void render(Graphics2D g) {
		g.setColor(Color.red);
		g.fillRect((int)(pos.x - SIZE/2), (int)(pos.y - SIZE/2), SIZE, SIZE);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
