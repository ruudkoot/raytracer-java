package tracer;

import java.util.Iterator;

/**
 * Represents a ray: a ray can `trace' itself, calculating a color.
 */
public class Ray {

	public Vec3 origin;
	public Vec3 direction;
	
	public Ray( Vec3 o, Vec3 d ) {
		origin = new Vec3( o );
		direction = new Vec3( d );	
	}
	
	/**
	 * Calculates the local light term, given an IntersectionInfo and a Light.
	 * This method does <b>not</b> do an occlusion test; do this yourself (e.g.
	 * using a shadow feeler).
	 */
	public Vec3 localLight( IntersectionInfo info, Light light ) {
		
		/* Calculate the amount of diffuse lighting.                          */
		Vec3 n = info.normal;
		Vec3 l = light.location.minus(info.location);
		l.normalize();
		
		Vec3 color;

		if ( !info.object.material.textured ) {
			color = info.object.material.color;
		} else {
			/* Compute the Perlin noise.                                      */
			
			float perlin = info.object.material.texture.n( info.location ); 
			
			perlin = Math.abs(perlin);
			color = info.object.material.color.times(1-perlin).add(info.object.material.perlin.times(perlin));
		}
		
		Vec3 diffuse = color.times(Math.max(0,n.dot(l)));
				
		/* Calculate the amount of specular lighting.                         */
		Vec3 e = origin.minus(info.location);
		e.normalize();
		Vec3 h = e.add(l);
		h.normalize();
				
		Vec3 specular = light.color.times((float)Math.pow(h.dot(n),info.object.material.specularPower));
		
		return diffuse.times(info.object.material.diffuse).add( specular.times(info.object.material.specular) );
	}
	
	/**
	 * Does the actual `raytracing'. Returns the color this ray `hits.'
	 * @param currentObject This object is ignored in the intersection tests; in
	 *    effect, this object is `invisible' to the ray. This is useful for
	 *    reflection rays and shadow feelers: it avoids precision-errors by just
	 *    ignoring the object you've just bounced off of. If all objects are
	 *    convex (which they are in this tracer) this is actually not a hack but
	 *    completely correct.
	 * @param maxReflectionsLeft Maximum recursion depth for reflection
	 *    calculations .
	 */
	public Vec3 trace( Traceable currentObject, int maxReflectionsLeft ) {
		// test all Traceable object in the scene for collision.
		// store the nearest one.
		IntersectionInfo nearestHit = null;
		Iterator i = Tracer.scene.iterator();
		while( i.hasNext() ) {
			
			Traceable t = (Traceable)i.next();
			if( t != currentObject ) {
				IntersectionInfo info = t.intersect( this );
				if(  info.hit  &&  (nearestHit==null || info.distance<nearestHit.distance)  ) {
					nearestHit = info;
				}
			}
			
		}
		if( nearestHit != null ) {
			// actually hit something
			Material material = nearestHit.object.material;
			Vec3 color = material.color.times( material.ambient ); // ambient
			
			// local contribution of light
			Iterator lightIter = Tracer.lights.iterator();
			while( lightIter.hasNext() ) {
				Light light = (Light)lightIter.next();
				
				/* Compute soft-shadowed lighting.                            */
				for ( int n = 0; n < light.numSamples; ++n ) {
					Vec3 shadowFeelerDirection = light.location.minus( nearestHit.location ).add(Vec3.random(light.size));
					Ray shadowFeeler = new Ray( nearestHit.location, shadowFeelerDirection );
					if( !shadowFeeler.hit( nearestHit.object ) ) {
						color = color.add(  localLight( nearestHit, light ).times(1.0f/light.numSamples)  );
					}
				}
				
			}
			
			// global illumination: add recursively computed reflection below this line
			
			if ( maxReflectionsLeft > 0 ) {
				
				Vec3 o, d, e, m, n, s, t;
				
				/* Construct a new vector from the point we just hit and      */
				/* calculate the refraction using the normal.                  */
				n = new Vec3( nearestHit.normal );
				e = new Vec3( direction.times(-1f) );
				o = new Vec3( nearestHit.location );
				d = new Vec3( e.times(-1f).add( n.times( n.dot(e) ).times(2f) ) );
				
				
				/* Monte-Carlo global illumination.                             */
				for ( int j = 0; j < material.numReflections; ++j ) {
					Ray r = new Ray ( o, d.add(Vec3.random(d.length()*material.reflectionJitter) ) );
					
					color = color.add( r.trace( nearestHit.object, maxReflectionsLeft - 1 ).times( nearestHit.object.material.reflectance ).times( 1.0f/material.numReflections ) );
				}
				
				/* Trace dielectric objects.                                  */
				if ( material.refractance > 0) {
					
					float nt = material.refractionIndex;
					
					/* incoming ray -> internal ray                           */
					d = direction;
					d.normalize();

					float D = 1 - ( ( 1 - (d.dot(n))*(d.dot(n)) ) / (nt*nt) );
					if (D < 0) return ( new Vec3(0,0,0) );
					
					t = new Vec3( (d.minus(n.times(d.dot(n))).times(1/nt)).minus( n.times((float)Math.sqrt(D)) ) );
					t.normalize();
					
					/* If we calls intersect with false as the second          *
					 * parameter it will return the intersection point the     *
					 * farthest away.                                         */
					IntersectionInfo u = ((Sphere)(nearestHit.object)).intersect( new Ray(o, t), false );
					
					/* internal ray -> outgoing ray                           */
					m = u.normal;
					d = t;
					d.normalize();
					
					D = 1 - ( (nt)*(nt)*( 1-d.dot(m)*d.dot(m) ) );
					if (D < 0) return ( new Vec3() );
					
					s = new Vec3( ((d.minus(m.times(d.dot(m)))).times(nt)).minus(m.times((float)Math.sqrt(D) ) ) );
					Ray r = new Ray(u.location, s );
					//Ray r = new Ray(o, t );
					
					color = color.add( r.trace( nearestHit.object, maxReflectionsLeft-1 ).times( material.refractance ) );
				}

			} 
			
			return ( color );
			
			
		} else {
			// hit nothing; return background color
			return new Vec3( 0.0f, 0.0f, 0.0f );
		}
		
	}
	
	/**
	 * Checks if the ray (origin + t*direction) hits the scene with 0 <= t <= 1.
	 * @param ignoreObject Similar to trace's currentObject parameter.
	 *    @see trace.
	 */
	public boolean hit( Traceable ignoreObject ) {
		
		Iterator i = Tracer.scene.iterator();
		while( i.hasNext() ) {
			
			Traceable t = (Traceable)i.next();
			if ( t != ignoreObject ) {
				if ( t.hit( this ) ) return ( true );
			}
			
		}
		
		return ( false );

	}
	
}