import java.awt.Color;


public class Main {
	public static double FOV = 1;
	public static final int DRAWDIST = 100;
	public static final double MOVESPEED = 0.2;
	public static final double TURNSPEED = 0.2;
	public static final double MAXPITCH = Math.PI/2.0;
	public static final double HALFPI = Math.PI/2.0;
	
	public static final int FRAMETIME = 1000/60;
	
	public static final Color FOGCOLOR = Color.white;
	public static final Color ERRORCOLOR = Color.black;
	
	public static void main(String args[]){
		Display disp = new Display();
		
		
		
		Vector3 pos = new Vector3(0, 0, 0);
		Matrix3 spaceRot = Matrix3.identity();
		double yaw = 0;
		double pitch = 0;
		//Region region = Region.randomium(10, 3);
		Region region = Maps.test4();
		
		//run loop
		while(true){
			long start = System.currentTimeMillis();
			
			//Apply turns
			if(disp.cam_d && pitch < MAXPITCH){
				pitch += TURNSPEED;
			}
			if(disp.cam_u && pitch > -1.0 * MAXPITCH){
				pitch -= TURNSPEED;
			}
			if(disp.cam_l){
				yaw -= TURNSPEED;
			}
			if(disp.cam_r){
				yaw += TURNSPEED;
			}
			Matrix3 rotMat = Matrix3.rotateY(yaw).mul(Matrix3.rotateX(pitch));
			//Apply moves
			Vector3 mv = new Vector3(0, 0, 0);
			if(disp.mov_f){
				mv.z += MOVESPEED;
			}
			if(disp.mov_b){
				mv.z -= MOVESPEED;
			}
			if(disp.mov_l){
				mv.x -= MOVESPEED;
			}
			if(disp.mov_r){
				mv.x += MOVESPEED;
			}
			
			//transform from movement coordinate space to current coordinate space
			pos = pos.add(spaceRot.mul(Matrix3.rotateY(yaw).mul(mv)));
			
			//Check collision or transition
			Transition trans = getTrans(pos);
			if(trans!=null){
				Region.Connection conn = region.conns[trans.dir];
				if(conn == null){//hit a wall
					pos = trans.intersect;
				}else {//going to new region
					region = conn.region;
					Matrix3 out = mOut(trans.dir);
					Matrix3 in = mIn(conn.dir);
					pos = out.mul(pos);
					pos = pos.add(new Vector3(0, 0, -2));
					pos = in.mul(pos);
					spaceRot = in.mul(out.mul(spaceRot));
				}
			}
			
			Vector3 scratchPos = new Vector3();
			int displayBuf[] = new int[Display.WIDTH * Display.HEIGHT];
			for(int y=0; y<Display.HEIGHT; y++){
				double fy = ((double)y / (double)Display.HEIGHT) - 0.5;
				for(int x=0; x<Display.WIDTH; x++){
					scratchPos.set(pos);
					double fx = (((double) x / (double)Display.WIDTH) - 0.5) * 16.0/9.0;
					//disp.graphics.setColor(trace(region, pos, spaceRot.mul(rotMat.mul(new Vector3(fx, -1.0 * fy, FOV)))));
					//disp.graphics.drawLine(x, y, x, y);
					int rgb = trace(region, scratchPos, spaceRot.mul(rotMat.mul(new Vector3(fx, -1.0 * fy, FOV))));
					displayBuf[x + Display.WIDTH * y] = rgb;
				}
			}
			synchronized(disp.image){
				disp.imagesrc = displayBuf;
				disp.image.notifyAll();
			}
			
			try {
				long end = System.currentTimeMillis();
				System.out.println("Ms to render frame: " + (end-start));
				Thread.sleep(Math.max(FRAMETIME - (end-start), 0));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static class Transition{
		public Vector3 intersect;
		public int dir;
		
		public Transition(Vector3 i, int d){
			intersect = i;
			dir = d;
		}
	}
	
	public static Transition getTrans(Vector3 v){
		int dir = -1;
		Vector3 dest = new Vector3(0, 0, 0);
		
		//front (z=1)
		if(v.z>1){
			dest = v.mul(1.0/v.z);
			dir = Region.FORWARD;
		}
			
		//back (z=-1)
		else if(v.z<-1){
			dest = v.mul(-1.0/v.z);
			dir = Region.BACK;
		}
			
		//up (y=1)
		else if(v.y>1){
			dest = v.mul(1.0/v.y);
			dir = Region.UP;
		}
			
		//down (y=-1)
		else if(v.y<-1){
			dest = v.mul(-1.0/v.y);
			dir = Region.DOWN;
		}
			
		//right (x=1)
		else if(v.x>1){
			dest = v.mul(1.0/v.x);
			dir = Region.RIGHT;
		}
		
		//left (x=-1)
		else if(v.x<-1){
			dest = v.mul(-1.0/v.x);
			dir = Region.LEFT;
		}
		
		if(dir==-1){
			return null;
		} else{
			return new Transition(dest, dir);
		}
	}
	
	//Preallocating things to save cycles
	private static Vector3 tDest = new Vector3();
	private static Vector3 tScratch = new Vector3();
	private static int tDir = -1;
	private static int tIter;
	private static double tF;
	private static boolean tDone;
	private static Region.Connection tC;
	public static int trace(Region r, Vector3 p, Vector3 v){
		
		for(tIter=0; tIter<DRAWDIST; tIter++){
			//TODO figure out how to do this less redundantly
			tDone = false;
			//front (z=1)
			if(v.z>0){
				tF = (1.0 - p.z) / v.z;
				tScratch.setAdd(p, v.mul(tF));
				if(inbounds(tScratch.x) && inbounds(tScratch.y)){
					tDir = Region.FORWARD;
					tDest.set(tScratch);
					tDone = true;
				}
			}
				
			//back (z=-1)
			if(v.z<0){
				tF = (-1.0 - p.z) / v.z;
				tScratch.setAdd(p, v.mul(tF));
				if(inbounds(tScratch.x) && inbounds(tScratch.y)){
					tDir = Region.BACK;
					tDest.set(tScratch);
					tDone = true;
				}
			}
				
			//up (y=1)
			if(!tDone && v.y>0){
				tF = (1.0 - p.y) / v.y;
				tScratch.setAdd(p, v.mul(tF));
				if(inbounds(tScratch.x) && inbounds(tScratch.z)){
					tDir = Region.UP;
					tDest.set(tScratch);
					tDone = true;
				}
			}
				
			//down (y=-1)
			if(!tDone && v.y<0){
				tF = (-1.0 - p.y) / v.y;
				tScratch.setAdd(p, v.mul(tF));
				if(inbounds(tScratch.x) && inbounds(tScratch.z)){
					tDir = Region.DOWN;
					tDest.set(tScratch);
					tDone = true;
				}
			}
				
			//right (x=1)
			if(!tDone && v.x>0){
				tF = (1.0 - p.x) / v.x;
				tScratch.setAdd(p, v.mul(tF));
				if(inbounds(tScratch.y) && inbounds(tScratch.z)){
					tDir = Region.RIGHT;
					tDest.set(tScratch);
					tDone = true;
				}
			}
			
			//left (x=-1)
			if(!tDone && v.x<0){
				tF = (-1.0 - p.x) / v.x;
				tScratch.setAdd(p, v.mul(tF));
				if(inbounds(tScratch.y) && inbounds(tScratch.z)){
					tDir = Region.LEFT;
					tDest.set(tScratch);
					tDone = true;
				}
			}
			
			if(!tDone){
				//Failed all somehow...
				return ERRORCOLOR.getRGB();
				
				
			}
				
			tC = r.conns[tDir];
			if(tC==null){
				transOut(tDest, tDir);
				return r.textureColor(tDir, tDest.x, tDest.y);
			}
			//System.out.println("Out: P: " + dest + ", V: " + v);
			
			transOut(v, tDir);
			transIn(v, tC.dir);
			transOut(tDest, tDir);
			tDest.z = -1;
			transIn(tDest, tC.dir);
			p.set(tDest);
			r = tC.region;
			
			//System.out.println("In:  P: " + p + ", V: " + v);
		}
		//Hit draw distance limit, draw fog
		return FOGCOLOR.getRGB();
	}
	
	public static boolean inbounds(double d){
		return d>=-1 && d<=1;
	}
	
	
	//Transform from normal to the space where dir=front
	private static double tx;
	private static double ty;
	private static double tz;
	public static Vector3 transOut(Vector3 v, int dir){
		tx = v.x;
		ty = v.y;
		tz = v.z;
		switch(dir){
		case Region.FORWARD:
			break;
		case Region.BACK:
			v.x= -1.0 * tx;
			v.y= ty;
			v.z= -1.0 * tz;
			break;
		case Region.UP:
			v.x= -1.0 * tx;
			v.y= tz;
			v.z= ty;
			break;
		case Region.DOWN:
			v.x= tx;
			v.y= tz;
			v.z= -1.0 * ty;
			break;
		case Region.RIGHT:
			v.x= -1.0 * tz;
			v.y= ty;
			v.z= tx;
			break;
		case Region.LEFT:
			v.x= tz;
			v.y= ty;
			v.z= -1.0 * tx;
			break;
		default:
			break;
		}
		return v;
	}
	
	//Transform from the space where dir=back to normal
	public static Vector3 transIn(Vector3 v, int dir){
		tx = v.x;
		ty = v.y;
		tz = v.z;
		switch(dir){
		case Region.FORWARD:
			v.x= -1.0 * tx;
			v.y= ty;
			v.z= -1.0 * tz;
			break;
		case Region.BACK:
			break;
		case Region.UP:
			v.x= tx;
			v.y= -1.0 * tz;
			v.z= ty;
			break;
		case Region.DOWN:
			v.x= -1.0 * tx;
			v.y= tz;
			v.z= ty;
			break;
		case Region.RIGHT:
			v.x= -1.0 * tz;
			v.y= ty;
			v.z= tx;
			break;
		case Region.LEFT:
			v.x= tz;
			v.y= ty;
			v.z= -1.0 * tx;
			break;
		
		default:
			break;
		}
		return v;
	}
	
	//same transformations as the above but optimized for readability rather than performance
	//normal -> dir=front
	public static Matrix3 mOut(int dir){
		switch(dir){
		case Region.FORWARD:
			return Matrix3.identity();
		case Region.BACK:
			return Matrix3.rotateY(Math.PI);
		case Region.UP:
			return Matrix3.rotateZ(Math.PI).mul(Matrix3.rotateX(HALFPI));
		case Region.DOWN:
			return Matrix3.rotateX(-1.0*HALFPI);
		case Region.RIGHT:
			return Matrix3.rotateY(-1.0*HALFPI);
		case Region.LEFT:
			return Matrix3.rotateY(HALFPI);
		default:
			//whatever
			return null;
		}
	}
	
	//dir=back->normal
	public static Matrix3 mIn(int dir){
		switch(dir){
		case Region.FORWARD:
			return Matrix3.rotateY(Math.PI);
		case Region.BACK:
			return Matrix3.identity();
		case Region.UP:
			return Matrix3.rotateX(HALFPI);
		case Region.DOWN:
			return Matrix3.rotateX(-1.0*HALFPI).mul(Matrix3.rotateZ(Math.PI));
		case Region.RIGHT:
			return Matrix3.rotateY(-1.0*HALFPI);
		case Region.LEFT:
			return Matrix3.rotateY(HALFPI);
		
		default:
			//whatever
			return null;
		}
	}
	
	public static Color colorFor(int dir){
		switch(dir){
		case Region.FORWARD:
			return Color.blue;
		case Region.BACK:
			return Color.green;
		case Region.UP:
			return Color.yellow;
		case Region.DOWN:
			return Color.red;
		case Region.RIGHT:
			return Color.orange;
		case Region.LEFT:
			return Color.cyan;
		default:
			//whatever
			return ERRORCOLOR;
		}
	}
}


