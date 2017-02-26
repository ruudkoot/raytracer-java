package tracer;

import java.io.IOException;

/**
 * Represents a point-light
 */
public class Light {
	
	public Light() {
		location = new Vec3();	
		color = new Vec3(1,1,1);
		intensity = 1;
		size = 0;
		numSamples = 1;
	}
	
	public Vec3 location;
	public Vec3 color;
	public float intensity;
	public float size;
	public int numSamples;

	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			if( p.tryKeyword("location") ) {
				location.parse( p );
			} else if( p.tryKeyword("color") ) {
				color.parse( p );
			} else if( p.tryKeyword("intensity") ) {
				intensity = p.parseFloat();
			} else if( p.tryKeyword("size") ) {
				size = p.parseFloat();
			} else if( p.tryKeyword("numsamples") ) {
				numSamples = (int)p.parseFloat();
			} else {
				System.out.println( p.tokenWasUnexpected() );	
			}
			
		}
	}
	
}