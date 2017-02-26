package tracer;
//import java.io.IOException; /* We don't need this one. */

/**
 * Represents a 4x4 matrix.
 * Note that all the `operator' methods return a <i>new</i> Mat4 object.
 */
public class Mat4 {
	
	public Vec4 r1, r2, r3, r4; // the for rows of the matrix, represented as Vec4s.
	
	public Mat4 add( Mat4 that ) {
		return new Mat4( r1.add(that.r1)
					   , r2.add(that.r2)
					   , r3.add(that.r3)
					   , r4.add(that.r4)
					   );
	}

	public Mat4 minus( Mat4 that ) {
		return new Mat4( r1.minus(that.r1)
					   , r2.minus(that.r2)
					   , r3.minus(that.r3)
					   , r4.minus(that.r4)
					   );
	}

	public Mat4 times( Mat4 that ) {
		Mat4 t = that.transposed();
		
		return new Mat4( new Vec4( r1.dot(t.r1), r1.dot(t.r2), r1.dot(t.r3), r1.dot(t.r4) )
				        , new Vec4( r2.dot(t.r1), r2.dot(t.r2), r2.dot(t.r3), r2.dot(t.r4) )
				        , new Vec4( r3.dot(t.r1), r3.dot(t.r2), r3.dot(t.r3), r3.dot(t.r4) )
				        , new Vec4( r4.dot(t.r1), r4.dot(t.r2), r4.dot(t.r3), r4.dot(t.r4) ) );

	}

	public Vec4 transform( Vec4 v ) {
		return new Vec4( r1.dot(v), r2.dot(v), r3.dot(v), r4.dot(v) );
	}
	
	public Mat4 inverse() {
		
		float invDet = 1/det();
		
		return new Mat4( new Vec4( cofac(0,0)*invDet, cofac(1,0)*invDet, cofac(2,0)*invDet, cofac(3,0)*invDet )
				        , new Vec4( cofac(0,1)*invDet, cofac(1,1)*invDet, cofac(2,1)*invDet, cofac(3,1)*invDet )
				        , new Vec4( cofac(0,2)*invDet, cofac(1,2)*invDet, cofac(2,2)*invDet, cofac(3,2)*invDet )
				        , new Vec4( cofac(0,3)*invDet, cofac(1,3)*invDet, cofac(2,3)*invDet, cofac(3,3)*invDet )
				        );
	}
	
	/* Calculates the cofactor of an element in a matrix.                     */
	public float cofac(int m, int n) {
		
		int k = 0, l = 0;
		float[][] det = new float[3][3];
		float[][] M = new float[4][4];
		
		M[0][0] = r1.x; M[0][1] = r1.y; M[0][2] = r1.z; M[0][3] = r1.w; 
		M[1][0] = r2.x; M[1][1] = r2.y; M[1][2] = r2.z; M[1][3] = r2.w;
		M[2][0] = r3.x; M[2][1] = r3.y; M[2][2] = r3.z; M[2][3] = r3.w;
		M[3][0] = r4.x; M[3][1] = r4.y; M[3][2] = r4.z; M[3][3] = r4.w;
		
		for( int i = 0; i < 4; ++i ) {
			if ( i == m ) ++i;
			if (i < 4) {
				for ( int j = 0; j < 4; ++j ) {
					if ( j == n ) ++j;
					if (j < 4) {
						det[k][l] = M[i][j];
					}
					l++;
				}
			}
			l = 0;
			k++;
		}
		
		return ( ( det[0][0]*(det[1][1]*det[2][2]-det[1][2]*det[2][1])
				 - det[0][1]*(det[1][0]*det[2][2]-det[1][2]*det[2][0])
				 + det[0][2]*(det[1][0]*det[2][1]-det[1][1]*det[2][0])
				 )
			   ) * ( (m+n)%2==0 ? 1 : -1 );

	}
	
	/* Calculates the determinant of a matrix.                                */
	public float det() {
		return ( r1.x*( r2.y*(r3.z*r4.w-r3.w*r4.z)
				       -r2.z*(r3.y*r4.w-r3.w*r4.y)
				       +r2.w*(r3.y*r4.z-r3.z*r4.y)
				       )
				- r1.y*( r2.x*(r3.z*r4.w-r3.w*r4.z)
					    -r2.z*(r3.x*r4.w-r3.w*r4.x)
					    +r2.w*(r3.x*r4.z-r3.z*r4.x)
					    )
				+ r1.z*( r2.x*(r3.y*r4.w-r3.w*r4.y)
					    -r2.y*(r3.x*r4.w-r3.w*r4.x)
					    +r2.w*(r3.x*r4.y-r3.y*r4.x)
					    )
				- r1.w*( r2.x*(r3.y*r4.z-r3.z*r4.y)
					    -r2.y*(r3.x*r4.z-r3.z*r4.x)
					    +r2.z*(r3.x*r4.y-r3.y*r4.x)
					    )
				);
	}
	
	public Mat4 transposed() {
		return new Mat4( new Vec4(r1.x, r2.x, r3.x, r4.x)
		               , new Vec4(r1.y, r2.y, r3.y, r4.y)
		               , new Vec4(r1.z, r2.z, r3.z, r4.z)
		               , new Vec4(r1.w, r2.w, r3.w, r4.w)
		               );
	}

	public Mat4( Vec4 r1, Vec4 r2, Vec4 r3, Vec4 r4 ) {
		this.r1 = new Vec4(r1);
		this.r2 = new Vec4(r2);
		this.r3 = new Vec4(r3);
		this.r4 = new Vec4(r4);
	}

	public Mat4() {
		r1 = new Vec4( 1, 0, 0, 0 );
		r2 = new Vec4( 0, 1, 0, 0 );
		r3 = new Vec4( 0, 0, 1, 0 );
		r4 = new Vec4( 0, 0, 0, 1 );
	}
	
}