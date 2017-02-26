package tracer;

import java.io.IOException;

/**
 * Holds shading parameters.
 */
public class Material {
	
	public Material() {
		color = new Vec3( 1, 1, 1 );
		perlin = new Vec3( 1, 1, 1 );
		textured = false;
		ambient = 0.2f;
		diffuse	= 0.8f;
		specular = 0.0f;
		specularPower = 16.0f;
		reflectance = 0.0f;
		numReflections = 1;
		reflectionJitter = 0.0f;
		texture = new Perlin();
		refractance = 0.0f;
		refractionIndex = 1.0f;		
	}
	
	public Vec3 color;
	public Vec3 perlin;
	public boolean textured;
	public float ambient;
	public float diffuse;
	public float specular;
	public float specularPower;
	public float reflectance;
	public int numReflections;
	public float reflectionJitter;
	public Texture texture;
	public float refractance;
	public float refractionIndex;
	
	
	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			if( p.tryKeyword("color") ) {
				color.parse( p );
				textured = false;
			} else if( p.tryKeyword("perlin") ) {
				color.parse( p );
				perlin.parse( p );
				textured = true;
			} else if( p.tryKeyword("ambient") ) {
				ambient = p.parseFloat();
			} else if( p.tryKeyword("diffuse") ) {
				diffuse = p.parseFloat();
			} else if( p.tryKeyword("specular") ) {
				specular = p.parseFloat();
			} else if( p.tryKeyword("specularpower") ) {
				specularPower = p.parseFloat();
			} else if( p.tryKeyword("reflectance") ) {
				reflectance = p.parseFloat();
			} else if( p.tryKeyword("numreflections") ) {
				numReflections = (int)p.parseFloat();
			} else if( p.tryKeyword("reflectionjitter") ) {
				reflectionJitter = p.parseFloat();	
			} else if( p.tryKeyword("refraction" ) ) { /* Documentation is ambiguous... */
				refractance = p.parseFloat();
			} else if( p.tryKeyword("refractance" ) ) { /* Documentation is ambiguous... */
				refractance = p.parseFloat();
			} else if( p.tryKeyword("refractionindex" ) ) {
				refractionIndex = p.parseFloat();
			} else {
				System.out.println( p.tokenWasUnexpected() );	
			}
			
		}
	}
	
}