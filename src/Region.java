import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;


public class Region {
	public static final int UP=0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int FORWARD = 4;
	public static final int BACK = 5;
	
	public static final int TEXTURESIZE = 256;
	
	public Connection conns[];
	public int textures[][];
	
	public static HashMap<String, BufferedImage> texMap = new HashMap<String, BufferedImage>();
	
	public static final String dickbutt = "dickbutt.png";
	
	
	
	public Region(){
		conns = new Connection[6];
		textures = new int[6][TEXTURESIZE * TEXTURESIZE];
		
		setTexture(UP, "ceiling.png");
		setTexture(DOWN, "floor.png");
		setTexture(FORWARD, "wall1.png");
		setTexture(RIGHT, "wall2.png");
		setTexture(BACK, "wall3.png");
		setTexture(LEFT, "wall4.png");
	}
	
	public void copyTextures(Region other){
		for(int i=0; i<textures.length; i++){
			for(int j=0; j<textures[0].length; j++){
				textures[i][j]=other.textures[i][j];
			}
		}
	}
	
	public BufferedImage getImg(String imgPath){
		if(!texMap.containsKey(imgPath)){
			try {
				BufferedImage img = ImageIO.read(new File(imgPath));
				System.out.println(imgPath + ": " + img.getWidth() + " x " + img.getHeight());
				texMap.put(imgPath, img);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return texMap.get(imgPath);
	}
	
	public void setTexture(int dir, String imgPath){
		int arr[] = textures[dir];
		getImg(imgPath).getRGB(0, 0, TEXTURESIZE, TEXTURESIZE, arr, 0, TEXTURESIZE);
	}
	
	public void addConn(int out, Region other, int in){
		conns[out]=new Connection(other, in);
		other.conns[in]= new Connection(this, out);
	}
	
	public void addHalfConn(int out, Region other, int in){
		conns[out]=new Connection(other, in);
	}
	
	public void removeConns(){
		//this assumes that all conns are two-way but whatever.
		for(Connection conn: conns){
			if(conn != null){
				conn.region.conns[conn.dir] = null;
			}
			conns = new Connection[6];
		}
	}
	
	public class Connection{
		public Region region;
		public int dir; //of entry
		
		public Connection(Region region, int dir){
			this.region = region;
			this.dir = dir;
		}
	}
	
	private int xp;
	private int yp;
	//with x, y in the [-1, 1] basis
	public int textureColor(int dir, double x, double y){
		xp = (int)(((x +1.0)/2.0) * TEXTURESIZE);
		yp = (int)(((y - 1.0)/-2.0) * TEXTURESIZE);
		
		if(xp >= TEXTURESIZE){
			xp = TEXTURESIZE - 1;
		}
		if(yp >= TEXTURESIZE){
			yp = TEXTURESIZE - 1;
		}
		
		//System.out.println("x: " + xp + " y: " + yp);
		return textures[dir][xp + yp * TEXTURESIZE];
		
	}
	
	public static Region randomium(int n, int saturation){
		Region r[] = new Region[n];
		Random rand = new Random();
		for(int i=0; i<n; i++){
			r[i] = new Region();
		}
		
		for(int i=0; i<n; i++){
			Region ri = r[i];
		
			for(int j=0; j<saturation; j++){
				int a = rand.nextInt(6);
				int b = rand.nextInt(6);
				int c = rand.nextInt(n);
			
				Region rc = r[c];
			
				if(c!=i && ri.conns[a] == null && rc.conns[b] == null){
					ri.addConn(a, rc, b);
				}
			}
		}
		
		return r[0];
	}
}
