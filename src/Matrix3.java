
public class Matrix3 {
	public double m[][];
	
	public Matrix3(){
		m = new double[3][3];
	}
	
	public Matrix3(double xx, double xy, double xz, double yx, double yy, double yz, double zx, double zy, double zz){
		m = new double[3][3];
		m[0][0] = xx;
		m[0][1] = xy;
		m[0][2] = xz;
		m[1][0] = yx;
		m[1][1] = yy;
		m[1][2] = yz;
		m[2][0] = zx;
		m[2][1] = zy;
		m[2][2] = zz;
	}
	
	public Matrix3(double args[]){
		m = new double[3][3];
		for(int r=0; r<3; r++){
			for(int c=0; c<3; c++){
				m[r][c] = args[r*3 + c];
			}
		}
	}
	
	public Matrix3(double[][] args){
		m = args;
	}
	
	public Matrix3 mul(Matrix3 other){
		double a[][] = new double[3][3];
		for(int r=0; r<3; r++){
			for(int c=0; c<3; c++){
				a[r][c] = row(r).dot(other.col(c));
			}
		}
		return new Matrix3(a);
	}
	
	public Vector3 mul(Vector3 v){
		return new Vector3(row(0).dot(v), row(1).dot(v), row(2).dot(v));
	}
	
	public Vector3 row(int r){
		return new Vector3(m[r][0], m[r][1], m[r][2]);
	}
	
	public Vector3 col(int c){
		return new Vector3(m[0][c], m[1][c], m[2][c]);
	}
	
	public static Matrix3 identity(){
		return new Matrix3(1, 0, 0,   
				           0, 1, 0,    
				           0, 0, 1);
	}
	
	public static Matrix3 rotateX(double theta){
		return new Matrix3(1, 0, 0,   
		           		   0, Math.cos(theta), -1 * Math.sin(theta),    
		                   0, Math.sin(theta), Math.cos(theta));
	}
	public static Matrix3 rotateY(double theta){
		return new Matrix3(Math.cos(theta), 0, Math.sin(theta),   
		           		   0, 1, 0,    
		                   -1 * Math.sin(theta), 0, Math.cos(theta));
	}
	public static Matrix3 rotateZ(double theta){
		return new Matrix3(Math.cos(theta), -1 * Math.sin(theta), 0,   
		           		   Math.sin(theta), Math.cos(theta), 0,    
		                   0, 0, 1);
	}
	
	
}
