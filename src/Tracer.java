package tracer;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.awt.image.MemoryImageSource;
import java.io.IOException;

/**
 * The main class. Displays the window and coordinates the raytracing.
 */
public class Tracer extends Frame {
	
	public Tracer() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}

	/* Avoid a warning with Java 5.                                           */
	static final long serialVersionUID = 0;
	
	static int maxReflectionDepth = 0;
	static float gamma = 1.0f;
	
	MemoryImageSource imageSource;
	Image offscreenImage;
	int[] pixelBuffer;
	public static Camera camera;
	public static ArrayList scene;
	public static ArrayList lights;
	public static int width = 400;
	public static int height = 400;
	
	/**
	 * Loads and parses the file "scene.txt". Adds the things it recognizes to
	 * the `scene' and `lights' ArrayLists.
	 */
	public void loadScene() {
		camera = new Camera();
		scene = new ArrayList();
		lights = new ArrayList();
		try {
			
			Parser p = new Parser( "scene.txt" );

			while( !p.endOfFile() ) {
				if( p.tryKeyword( "width" ) ) {
					width = (int)p.parseFloat();
				} else if( p.tryKeyword( "height" ) ) {
					height = (int)p.parseFloat();
				} else if( p.tryKeyword( "maxreflectiondepth" ) ) {
					maxReflectionDepth = (int)p.parseFloat();
				} else if( p.tryKeyword( "gamma" ) ) {
					gamma = p.parseFloat();
				} else if( p.tryKeyword( "sphere" ) ) {
					Sphere s = new Sphere();
					s.parse( p );
					scene.add( s );
				} else if( p.tryKeyword( "plane" ) ) {
					Plane pl = new Plane();
					pl.parse( p );
					scene.add( pl );
				} else if( p.tryKeyword( "triangle" ) ) {
					Triangle pl = new Triangle();
					pl.parse( p );
					scene.add( pl );
				} else if( p.tryKeyword( "camera" ) ) {
					camera.parse( p );
				} else if( p.tryKeyword( "light" ) ) {
					Light l = new Light();
					l.parse( p );
					lights.add( l );
				} else {
					System.out.println( p.tokenWasUnexpected() );		
				}
			}			
			
			
		} catch( IOException e ) {}
	}
	
	/**
	 * Redraws the offscreenImage onto the screen.
	 */
	public void paint( Graphics gr ) {
		if( offscreenImage != null ) {
			if( pixelBuffer != null ) {
				imageSource.newPixels();
			}
			gr.drawImage( offscreenImage, 0, 50, null );
		}
	}
	
	/**
	 * The actual raytracing starts here.
	 * Initializes the pixel buffers and raytraces each pixel.
	 * Redraws the screen each time an additional 8 rows have been rendered.
	 */
	public void render() {
		pixelBuffer = new int[ width*height ];
		imageSource = new MemoryImageSource( width, height, pixelBuffer, 0, width );
		imageSource.setAnimated( true );
		offscreenImage = Toolkit.getDefaultToolkit().createImage( imageSource );
		
		ToneMapper toneMapper = new ToneMapper( gamma );
		
		System.out.println( "Starting raytracing." );
		
		camera.init();
		
		for( int y=0; y<height*camera.antiAlias; y+=camera.antiAlias ) {
			for( int x=0; x<width*camera.antiAlias; x+=camera.antiAlias ) {
		
				int index = (height-y/camera.antiAlias-1)*width + x/camera.antiAlias;
				Vec3 color = new Vec3();
				
				/* Perform anti-aliasing.                                     */
				for ( int i = 0; i < camera.antiAlias; ++i ) {
					for ( int j = 0; j < camera.antiAlias; ++j ) {
						color = color.add( tracePixel( x+i, y+j ) );
					}
				}
				
				color = color.times( 1.0f/(camera.antiAlias*camera.antiAlias) );
				
				pixelBuffer[ index ] = toneMapper.map( color.x, color.y, color.z );

				
			}
			
			paint( getGraphics() ); 			
		}
		
		System.out.println( "Finished raytracing." );
		
	}
	
	/**
	 * Given (x,y) coordinates of the pixel to be traced, constructs the primary
	 * ray, raytraces it (by calling Ray.trace) and returns the result (the
	 * color for the pixel).
	 */
	public Vec3 tracePixel( int x, int y ) {
		
		// compute a ray from the origin of the camera through the center of pixel (x,y)
		// replace the line below by meaningful code
		
		Vec4 direction = new Vec4( (float) (((camera.right-camera.left)/(width*camera.antiAlias))*(x+0.5) + camera.left)
				                 , (float) (((camera.top-camera.bottom)/(height*camera.antiAlias))*(y+0.5) + camera.bottom)
								 , (float) camera.near
								 , 1.0f
				                 );
		
		//direction = direction.minus(camera.origin);
		Vec3 dir = new Vec3( camera.M.transform( direction ) );
		dir.normalize();

		Ray r = new Ray( camera.origin, dir );
		return r.trace( null, maxReflectionDepth );
		
	}

	public static void main(String args[]) {
	
		Tracer mainFrame = new Tracer();
		mainFrame.setTitle("Graphics: Practicum 2");
		
		System.out.println( "Parsing scene description." );
		mainFrame.loadScene();
		mainFrame.setSize(Tracer.width+10, Tracer.height+50);
		mainFrame.setVisible(true);
		mainFrame.render();
		
	}
}
