//repush


// run java NBody 157788000.0 25000.0 data/<file>.txt at command line
public class NBody {
	public static void main(String[] args) {
		double T = Double.parseDouble(args[0]);
		double dt = Double.parseDouble(args[1]);
		String filename = args[2];
		In in = new In(filename);

		int numPlanets = in.readInt();
		double radius = in.readDouble();
		Planet[] planets = new Planet[numPlanets];

		for (int x=0; x < numPlanets; x++) {
			planets[x] = getPlanet(in);
		}
		StdDraw.setScale(-radius, radius);
		StdDraw.picture(0, 0, "images/starfield.jpg");
		for (Planet p:planets) {
			p.draw();
		}
	    //StdAudio.loop("audio/drk_cut.wav");
	    // audio not looping for long; .play() doesn't work
		for (int t=0; t<T ;t+=dt) {	
			for (Planet p:planets) {
				p.setNetForce(planets);
			}
			for (Planet p:planets){
				p.update(dt);
			}
			StdDraw.picture(0, 0, "images/starfield.jpg");
			for (Planet p:planets) {
				p.xNetForce = 0; 
				p.yNetForce = 0;
				p.draw();
			}
				StdDraw.show(10);
			}

			
		StdOut.printf("%d\n", numPlanets);
        StdOut.printf("%.2e\n", radius);

        for (int i = 0; i < numPlanets; i++) {
        StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                   planets[i].x, planets[i].y, planets[i].xVelocity,
                   planets[i].yVelocity, planets[i].mass, planets[i].img);
         }

		
	}

	public static Planet getPlanet(In space) {
		Planet p = new Planet(space.readDouble(), space.readDouble(),
			                  space.readDouble(), space.readDouble(),
			                  space.readDouble(), space.readString());
		return p;
	}
}