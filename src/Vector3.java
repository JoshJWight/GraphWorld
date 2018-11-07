
public class Vector3 {
	public double x;
	public double y;
	public double z;
	
	public Vector3(){
		
	}
	
	public Vector3(double x, double y, double z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public Vector3 mul(double c){
		return new Vector3(x*c, y*c, z*c);
	}
	
	public Vector3 mul(Matrix3 mat){
		return new Vector3(dot(mat.col(0)), dot(mat.col(1)), dot(mat.col(2)));
	}
	
	public Vector3 add(Vector3 other){
		return new Vector3(x + other.x, y + other.y, z + other.z);
	}
	
	public Vector3 sub(Vector3 other){
		return new Vector3(x - other.x, y - other.y, z - other.z);
	}
	
	public double dot(Vector3 other){
		return x*other.x + y*other.y + z*other.z;
	}
	
	public void set(Vector3 other){
		x = other.x;
		y = other.y;
		z = other.z;
	}
	
	public void setAdd(Vector3 a, Vector3 b){
		x = a.x + b.x;
		y = a.y + b.y;
		z = a.z + b.z;
	}
	
	public String toString(){
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
