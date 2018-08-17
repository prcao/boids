package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import main.util.Vector;

public class Input implements MouseListener, MouseMotionListener{

	public static Vector mousePos = new Vector();
	
	public Input(){
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		mousePos.x = e.getX();
		mousePos.y = e.getY();
		
		new Block(e.getX(), e.getY());
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

	@Override
	public void mouseDragged(MouseEvent e) {
		new Block(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

}
