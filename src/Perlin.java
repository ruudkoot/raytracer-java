package tracer;

/* Perlin Noise texture.                                                      */

public class Perlin implements Texture {
	
	Vec3[] G = new Vec3[256];
	int[] P = new int[256];
	
	public Perlin() {
		
		/* Initialize G.                                                      */
		for ( int i = 0; i < 256; ++i ) {
			float x = (float)(2.0*Math.random()-1.0);
			float y = (float)(2.0*Math.random()-1.0);
			float z = (float)(2.0*Math.random()-1.0);
			
			if (x*x+y*y+z*z < 1) {
				G[i] = new Vec3(x, y, z);
				G[i].normalize();
			} else { /* try again */
				--i;
			}
		}
		
        /* Initialize P.                                                      */
		for (int i = 0; i < 256; ++i ) {
			P[i] = i;
		}
		
		/* Make P a random permutation of itself.                             */
		for (int i = 0; i < 256; ++i ) {
			int a = (int)(255*Math.random());
			int b = (int)(255*Math.random());
			
			int t = P[a];
			P[a] = P[b];
			P[b] = t;
		}
	}
	
	public float n( Vec3 v ) {
		
		float s = 0;
		
		for ( int i = (int)Math.floor(v.x); i <= ((int)Math.floor(v.x)) + 1; ++i ) {
			for ( int j = (int)Math.floor(v.y); j <= ((int)Math.floor(v.y)) + 1; ++j ) {
				for ( int k = (int)Math.floor(v.z); k <= ((int)Math.floor(v.z)) + 1; ++k ) {
					s = s + omega(i, j, k, new Vec3(v.x - i, v.y - j, v.z - k) );
				}	
			}	
		}
		
		return ( s );
	}
	
	public float omega(int i, int j, int k, Vec3 v) {
		return ( weight(v.x) * weight(v.y) * weight(v.z) * ( gamma(i, j, k).dot(v) ) );
	}
	
	public float weight(float t) {
		if ( Math.abs(t) < 1.0 ) {
			return (float)( 2 * Math.pow(Math.abs(t), 3) - 3 * Math.pow(Math.abs(t), 2) + 1);
		} else {
			return ( 0.0f );
		}
	}

	public Vec3 gamma( int i, int j, int k ) {
		return G[phi(i+phi(j+phi(k)))];
	}
	
	public int phi( int i ) {
		return ( P[(int)Math.abs( i % 256 )] );
	}
	
}
