package tracer;

import java.io.IOException;

/**
 * 3D flat, unbounded, traceable thingy, represented by normal vector and distance from the origin
 */
public class Plane extends Traceable {

	Vec3 normal;
	float offset;
	
	public Plane( Vec3 n, float o ) {
		normal = new Vec3(n);
		normal.normalize();
		offset = o;
		material = new Material();
	}
	public Plane() {
		normal = new Vec3(0,1,0);
		offset = 0.0f;
		material = new Material();
	}
	
	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			if( p.tryKeyword("normal") ) {
				normal.parse( p );
			} else if( p.tryKeyword("offset") ) {
				offset = p.parseFloat();
			} else if( p.tryKeyword("material") ) {
				material.parse( p );
			} else {
				System.out.println( p.tokenWasUnexpected() );	
			}
			
		}
	}
	
	public IntersectionInfo intersect( Ray r ) {
		
		Vec3 n = normal;
		Vec3 p = r.origin;
		Vec3 v = r.direction;
		float d = offset;
		
		/* Make sure we are not parallel to the plane.                        */
		if ( n.dot(v) == 0 ) {
			return new IntersectionInfo( false );
		}
		
		/* We add instead of substract d to fix a bug in the scene file.      */
		float t = -((n.dot(p)+d)/n.dot(v));
		
		/* Make sure we are only tracing the ray forward.                     */
		if ( t < 0 ) {
			return new IntersectionInfo( false );
		}
		
		return new IntersectionInfo(p.add(v.times(t)), n, v.times(t).length(), this);

	}
	
	public boolean hit( Ray r ) {
		
		Vec3 n = normal;
		Vec3 p = r.origin;
		Vec3 v = r.direction;
		float d = offset;
		
		/* Make sure we are not parallel to the plane.                        */
		if ( n.dot(v) == 0 ) {
			return ( false );
		} else {
			float t = -((n.dot(p)+d)/n.dot(v));
			
			/* Make sure we are between de origin of the ray and the lamp.    */
			if ( t > 0 && t <= 1 ) {
				return ( true );
			} else {
				return ( false );
			}
			
		}
	}
}
	