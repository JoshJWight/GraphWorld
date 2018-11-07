import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;


public class Display extends JFrame implements KeyListener{
	public static int WIDTH=1280;//;
	//public static int WIDTH=960;
	public static int HEIGHT=720;
	//public static int HEIGHT=540;
	
	public Graphics graphics;
	public BufferedImage image;
	public int imagesrc[];
	public Thread thread;
	
	//key states
	public boolean mov_f;
	public boolean mov_b;
	public boolean mov_l;
	public boolean mov_r;
	public boolean cam_u;
	public boolean cam_d;
	public boolean cam_l;
	public boolean cam_r;
	
	public Display(){
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	System.exit(0);
		    }
		});
		this.setTitle("Raytrace Demo");
		this.setSize(WIDTH, HEIGHT);
		this.addKeyListener(this);
		this.setVisible(true);
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		imagesrc = new int[WIDTH * HEIGHT];
		graphics = image.getGraphics();
		
		thread = new Thread(){
			public void run(){
				while(true){
					try {
						synchronized(image){
							image.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					repaint();
				}
			}
		};
		thread.start();
	}
	
	public void paint(Graphics graphics){
		image.setRGB(0, 0, WIDTH, HEIGHT, imagesrc, 0, WIDTH);
		graphics.drawImage(image, 0, 0, null);
	}
	
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP:
			cam_u = true;
			break;
		case KeyEvent.VK_DOWN:
			cam_d = true;
			break;
		case KeyEvent.VK_RIGHT:
			cam_r = true;
			break;
		case KeyEvent.VK_LEFT:
			cam_l = true;
			break;
		case KeyEvent.VK_W:
			mov_f = true;
			break;
		case KeyEvent.VK_S:
			mov_b = true;
			break;
		case KeyEvent.VK_A:
			mov_l = true;
			break;
		case KeyEvent.VK_D:
			mov_r = true;
			break;
		default:
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP:
			cam_u = false;
			break;
		case KeyEvent.VK_DOWN:
			cam_d = false;
			break;
		case KeyEvent.VK_RIGHT:
			cam_r = false;
			break;
		case KeyEvent.VK_LEFT:
			cam_l = false;
			break;
		case KeyEvent.VK_W:
			mov_f = false;
			break;
		case KeyEvent.VK_S:
			mov_b = false;
			break;
		case KeyEvent.VK_A:
			mov_l = false;
			break;
		case KeyEvent.VK_D:
			mov_r = false;
			break;
		default:
			break;
		}
	}

	public void keyTyped(KeyEvent arg0) {
		//Nothing to do here
	}
}
