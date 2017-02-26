package tracer;

import java.io.IOException;

/* Tracet een convex figuur met drie hoekpunten. */
public class Triangle extends Traceable {

	Vec3 a, b, c;

	public Triangle( Vec3 a, Vec3 b, Vec3 c ) {
		this.a = new Vec3(a);
		this.b = new Vec3(c);
		this.c = new Vec3(c);
		material = new Material();
	}
	public Triangle() {
		a = new Vec3();
		b = new Vec3();
		c = new Vec3();
		material = new Material();
	}
	
	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			a.parse(p);
			b.parse(p);
			c.parse(p);
			
			if( p.tryKeyword("material") ) {
				material.parse( p );
			} else {
				System.out.println( p.tokenWasUnexpected() );	
			}

		}
	}
	
	public IntersectionInfo intersect( Ray r ) {
		
		Mat4 A = new Mat4( new Vec4( a.x - b.x, a.x - c.x, r.direction.x, 0 )
				          , new Vec4( a.y - b.y, a.y - c.y, r.direction.y, 0 )
				          , new Vec4( a.z - b.z, a.z - c.z, r.direction.z, 0 )
				          , new Vec4( 0, 0, 0, 1 )
				          );
		
		Vec4 v = new Vec4( a.x - r.origin.x
				          , a.y - r.origin.y
				          , a.z - r.origin.z
				          , 1
				          );
		            
		Vec4 s = A.inverse().transform(v);
		
		if ( s.z > 0 && s.y > 0 && s.y < 1 && s.x > 0 && s.x < 1.0f - s.y ) {
			Vec3 n = (b.minus(a)).cross(c.minus(a));
			n.normalize();

			return new IntersectionInfo( r.origin.add(r.direction.times(s.z)), n, r.direction.times(s.z).length(), this );
		} else {
			return new IntersectionInfo(false);
		}
		
	}
	
	public boolean hit( Ray r ) {
		
		Mat4 A = new Mat4( new Vec4( a.x - b.x, a.x - c.x, r.direction.x, 0 )
				          , new Vec4( a.y - b.y, a.y - c.y, r.direction.y, 0 )
				          , new Vec4( a.z - b.z, a.z - c.z, r.direction.z, 0 )
				          , new Vec4( 0, 0, 0, 1 )
				          );
		
		Vec4 v = new Vec4( a.x - r.origin.x
				          , a.y - r.origin.y
				          , a.z - r.origin.z
				          , 1
				          );
		          
		Vec4 s = A.inverse().transform(v);
		
		if ( s.z > 0 && s.y > 0 && s.y < 1 && s.x > 0 && s.x < 1.0f - s.y ) {
			return ( true );
		} else {
			return ( false );
		}
	}
}
	