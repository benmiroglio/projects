//repush
public class Planet {

	public double x;
	public double y;
	public double xVelocity;
	public double yVelocity;
	public double mass;
	public String img;
	public double xNetForce;
	public double yNetForce;
	public double xAccel;
	public double yAccel;
	//public double xVelocity;
	//public double yVelocity;


	public Planet(double xPos, double yPos, double xVel, double yVel,
		          double area, String name) {
		x = xPos;
		y = yPos;
		xVelocity = xVel;
		yVelocity = yVel;
		mass = area;
		img = name;

	}

	public double calcDistance(Planet p) {
		return Math.sqrt((p.x - x)*(p.x - x) + (p.y - y)*(p.y - y));
	}

	public double calcPairwiseForce(Planet p) {
		double g = 6.67e-11;
		return g*mass*p.mass / (calcDistance(p)*calcDistance(p));
	}

	public double calcPairwiseForceX(Planet p) {
		return calcPairwiseForce(p)*(p.x - x)/calcDistance(p);
	}

	public double calcPairwiseForceY(Planet p) {
		return calcPairwiseForce(p)*(p.y - y)/calcDistance(p);
	}

	public void setNetForce(Planet[] planets) {
         for (Planet p:planets) {
         	if (!p.equals(this)) {
         		xNetForce += calcPairwiseForceX(p);
         		yNetForce += calcPairwiseForceY(p);
         	}
         }
	}

	public void draw() {
		String file = String.format("images/%s", img);
		StdDraw.picture(x, y, file);
	} 

	public void update(double dt) {
		xAccel = xNetForce / mass;
		yAccel = yNetForce / mass;
		xVelocity = xVelocity + dt*xAccel;
		yVelocity = yVelocity + dt*yAccel;
		x = x + dt*xVelocity; 
		y = y + dt*yVelocity;
	}


}











