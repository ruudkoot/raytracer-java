package tracer;

import java.io.IOException;

/**
 * A 3D roundish traceable thingy with a center and a radius
 */
public class Sphere extends Traceable {

	Vec3 origin;
	float radius;
	
	public Sphere( Vec3 o, float r ) {
		origin = new Vec3(o);
		radius = r;	
		material = new Material();
	}
	public Sphere() {
		origin = new Vec3();
		material = new Material();	
	}
	
	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			if( p.tryKeyword("origin") ) {
				origin.parse( p );
			} else if( p.tryKeyword("radius") ) {
				radius = p.parseFloat();
			} else if( p.tryKeyword("material") ) {
				material.parse( p );
			} else {
				System.out.println( p.tokenWasUnexpected() );	
			}
			
		}
	}
	
	public IntersectionInfo intersect( Ray ray ) {
		return ( intersect( ray, true ) );
	}
	
	public IntersectionInfo intersect( Ray ray, boolean near ) {
		// delete the line of code below, and properly compute if the Ray r hits the Spere, and if so, 
		// record the nearest intersection point in the IntersectionInfo record, as well as the distance
		// of this intersection point to the origin of the ray. Initially, you may put a zero normal vector
		// in the IntersectionInfo, but as soon as we compute the local lighting model, you have to compute
		// a proper normal vector of the sphere at the intersection point. 
		
		Vec3 n;
		Vec3 p = ray.origin;
		Vec3 v = ray.direction;
		Vec3 c = origin;
		float r = radius;
		
		/* Calculate the disciminant.                                         */
		float D = 4*c.minus(p).dot(v)*c.minus(p).dot(v)-4*(c.minus(p).lengthSquared()-(r*r))*v.lengthSquared();

		if (D < 0) {
			return new IntersectionInfo(false);
		}
				
		/* Calculate the intersection point(s).                               */
		float t1 = (float)((c.minus(p).dot(v)-0.5*Math.sqrt(D))/v.lengthSquared());
		
		float t2;
		
		if (D != 0 ) {
			t2 = (float)((c.minus(p).dot(v)+0.5*Math.sqrt(D))/v.lengthSquared());
		} else {
			t2 = t1;
		}
		
		/* Return the intersection point that is nearest and in front of the  */
		/* origin.                                                            */
		if ( t1 > 0 && ((near? t1 < t2 : t1 > t2) || t2 < 0) ) {
			 n = new Vec3( v.times(t1).minus(c.minus(p)) );
			 n.normalize();
			 return ( new IntersectionInfo(v.times(t1).add(p), n, v.times(t1).length(), this) );
		} else if ( t2 > 0 ) {
			n = new Vec3( v.times(t2).minus(c.minus(p)) );
			n.normalize();
			return ( new IntersectionInfo(v.times(t2).add(p), n, v.times(t2).length(), this) );
		} else {
			return ( new IntersectionInfo( false ) );
		}
	}
	
	public boolean hit( Ray ray ) {
		
		Vec3 p = ray.origin;
		Vec3 v = ray.direction;
		Vec3 c = origin;
		float r = radius;
		
		/* Calculate the disciminant.                                         */
		float D = 4*c.minus(p).dot(v)*c.minus(p).dot(v)-4*(c.minus(p).lengthSquared()-(r*r))*v.lengthSquared();

		if (D < 0) {
			return ( false );
		}
				
		/* Calculate the intersection point(s).                               */
		float t1 = (float)((c.minus(p).dot(v)-0.5*Math.sqrt(D))/v.lengthSquared());
		
		float t2;
		
		if (D != 0 ) {
			t2 = (float)((c.minus(p).dot(v)+0.5*Math.sqrt(D))/v.lengthSquared());
		} else {
			t2 = t1;
		}
		
		/* Check if one of the intersectionpoints is between the origin       */
		/* (camera) and the lamp.                                             */
		if ( (t1 > 0 && t1 <= 1.0f) || (t2 > 0 && t2 <= 1.0f) ) {
			 return ( true );
		} else {
			return ( false );
		}
	}
}