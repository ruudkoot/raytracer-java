package tracer;

import java.io.IOException;

/**
 * Represents the camera:
 * viewing window = [left,bottom] x [right,top], with z-coordinate <b>near</b>
 */
public class Camera {
	
	public Camera() {
		origin = new Vec3(  0,  0,  0);
		target = new Vec3(  0,  0, -1);
		viewUp = new Vec3(  0,  1,  0);
		left = -1;
		bottom = -1;
		right = 1;
		top = 1;
		near = -1;
		antiAlias = 2;
	}
	
	public void init() {
		
		Vec3 u, v, w;
		
		/* Make sure (u,v,w) is an orthonarmal basis.                         */
		w = (target.minus(origin).times(-1.0f));
		w.normalize();
		
		u = viewUp.cross(w);
		u.normalize();
		
		v = w.cross(u);
		v.normalize();
		
		/* Calculate the transformation matrix.                               */
		//Mat4 A = new Mat4( new Vec4(1, 0, 0, origin.x)
		//		          , new Vec4(0, 1, 0, origin.y)
		//		          , new Vec4(0, 0, 1, origin.z)
		//		          , new Vec4(0, 0, 0,         1) );
		
		Mat4 B = new Mat4( new Vec4(u.x, v.x, w.x, 0)
				          , new Vec4(u.y, v.y, w.y, 0)
				          , new Vec4(u.z, v.z, w.z, 0)
				          , new Vec4(  0,   0,   0, 1) );
		
		M = B; //A.times(B);
	}
	
	public Vec3 origin;
	public Vec3 target;
	public Vec3 viewUp;
	public float left;
	public float bottom;
	public float right;
	public float top;
	public float near;
	public int antiAlias;
	public Mat4 M;

	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			if( p.tryKeyword("origin") ) {
				origin.parse( p );
			} else if( p.tryKeyword("target") ) {
				target.parse( p );
			} else if( p.tryKeyword("viewup") ) {
				viewUp.parse( p );
			} else if( p.tryKeyword("window") ) {
				p.parseKeyword( "<" );
				left = p.parseFloat();
				p.parseKeyword( "," );
				bottom = p.parseFloat();
				p.parseKeyword( "," );
				right = p.parseFloat();
				p.parseKeyword( "," );
				top = p.parseFloat();
				p.parseKeyword( ">" );
			} else if( p.tryKeyword("near") ) {
				near = p.parseFloat();
			} else if( p.tryKeyword("sampleroot") ) {
				antiAlias = (int)p.parseFloat();
			} else {
				System.out.println( p.tokenWasUnexpected() );	
			}
			
		}
	}
	
}