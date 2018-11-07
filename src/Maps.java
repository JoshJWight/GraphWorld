
public class Maps {
	public static final int UP = Region.UP;
	public static final int DOWN = Region.DOWN;
	public static final int FORWARD = Region.FORWARD;
	public static final int BACK = Region.BACK;
	public static final int LEFT = Region.LEFT;
	public static final int RIGHT = Region.RIGHT;
	
	public static Region test1(){
		Region a = new Region();
		Region b = new Region();
		Region c = new Region();
		a.addConn(Region.FORWARD, b, Region.BACK);
		b.addConn(Region.FORWARD, a, Region.BACK);
		b.addConn(Region.RIGHT, c, Region.BACK);
		c.addConn(Region.FORWARD, b, Region.LEFT);
		return a;
	}
	
	public static Region test2(){
		Region a = new Region();
		Region b = new Region();
		Region c = new Region();
		Region d = new Region();
		a.addConn(Region.FORWARD, b, Region.BACK);
		b.addConn(Region.FORWARD, c, Region.BACK);
		c.addConn(Region.FORWARD,  d, Region.BACK);
		d.addConn(Region.FORWARD, a, Region.RIGHT);
		
		Region hall = new Region();
		c.addConn(Region.LEFT, hall, Region.UP);
		for(int i=0; i<3; i++){
			Region hall2 = new Region();
			hall.addConn(Region.DOWN, hall2, Region.UP);
			hall = hall2;
		}
		hall.setTexture(Region.DOWN, Region.dickbutt);
		
		return a;
		
	}
	
	public static Region test3(){
		Region a = new Region();
		Region r = a;
		String paths[] = {"snow.png", "flagstones.png", "grass.png", "metal.png", "carpet.png"};
		
	
		for(int i=0; i<5; i++){
			System.out.println(i);
			Region next = new Region();
			next.addConn(Region.BACK, r, Region.FORWARD);
			Region template = new Region();
			template.setTexture(Region.DOWN, paths[i]);
			Region[][][] c1 = cube(3, 3, 3, template);
			Region[][][] c2 = cube(3, 3, 3, template);
			next.addConn(Region.LEFT, c1[2][0][1], Region.RIGHT);
			next.addConn(Region.RIGHT, c2[0][0][1], Region.LEFT);
			r = next;
		}
		return a;
	}
	
	public static Region test4(){
		Region template = new Region();
		Region[][][] cube = cube(3, 3, 3, template);
		cube[1][1][1].removeConns();
		Region a = new Region();
		for(int i=0; i<6; i++){
			a.setTexture(i, "flagstones.png");
		}
		cube[1][0][1].addHalfConn(UP, a, DOWN);
		cube[1][2][1].addHalfConn(DOWN, a, UP);
		cube[0][1][1].addHalfConn(RIGHT, a, LEFT);
		cube[2][1][1].addHalfConn(LEFT, a, RIGHT);
		cube[1][1][0].addHalfConn(FORWARD, a, BACK);
		cube[1][1][2].addHalfConn(BACK, a, FORWARD);
		
		return cube[0][0][0];
	}
	
	//Modules
	public static Region[][][] cube(int width, int height, int depth, Region template){
		Region[][][] c = new Region[width][height][depth];
		for(int x=0; x<width; x++){
			for(int y=0; y<height; y++){
				for(int z=0; z<height; z++){
					c[x][y][z] = new Region();
					c[x][y][z].copyTextures(template);
					if(x>0){
						c[x][y][z].addConn(Region.LEFT, c[x-1][y][z], Region.RIGHT);
					}
					if(y>0){
						c[x][y][z].addConn(Region.DOWN, c[x][y-1][z], Region.UP);
					}
					if(z>0){
						c[x][y][z].addConn(Region.BACK, c[x][y][z-1], Region.FORWARD);
					}
				}
			}
		}
		return c;
	}
}
