package com.android.shuomi.groupon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.android.shuomi.ServiceListView;
import com.android.shuomi.StreamReader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AsyncImageLoader {
	private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	
	public Drawable loadDrawable( final String imageUrl, final ImageCallback callback ) {
		if ( imageCache.containsKey( imageUrl ) ) {
			SoftReference<Drawable> softReference = imageCache.get( imageUrl );
			if ( softReference.get() != null ) {
				return softReference.get();
			}
		}
		
		final Handler handler = new Handler() {
			@Override
			public void handleMessage( Message msg ) {
				if ( (Drawable) msg.obj != null ) {
					callback.imageLoaded( (Drawable) msg.obj, imageUrl );
				}
			}
		};
		
		new Thread() {
			public void run() {
				Drawable drawable = loadImageFromUrl( imageUrl );
				
				if ( drawable != null ) {
					imageCache.put( imageUrl, new SoftReference<Drawable>( drawable ) );
				}
				
				handler.sendMessage( handler.obtainMessage( 0, drawable ) );
			};
		}.start();
		
		return null;
	}
	
	protected Drawable loadImageFromUrl( String imageUrl ) 
	{
		Drawable image = null;
		try 
		{
			InputStream imageStream = new URL(imageUrl).openStream();
			image = Drawable.createFromStream( imageStream, "src" );
		}
		catch ( Exception e ) 
		{
			Log.w( "AsyncImageLoader", e.getMessage() );
		}
		
		return image;
	}
	
	private Drawable readCacheImage( String file )
	{
//		File file = new File
//		   InputStream in = null;
//		   try {
//		     in = new BufferedInputStream(new FileInputStream(file));
//		     ...
//		    finally {
//		     if (in != null) {
//		       in.close();
//		     }
//		   }
//		 }
		Drawable image = null;
		
		try 
		{
			InputStream imageStream = ServiceListView.gContext.openFileInput( file );
			image = Drawable.createFromStream( imageStream, "src" );			
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return image;
	}
	
	private void cacheImage( InputStream is )
	{
		StreamReader reader = new StreamReader( is, 0 );
		
		if ( reader.size() > 0 )
		{
			FileOutputStream fos;
			try 
			{
				fos = ServiceListView.gContext.openFileOutput( String.valueOf( System.currentTimeMillis() ), Context.MODE_PRIVATE );
				fos.write( reader.getString().getBytes() ); 
				fos.close();
			}				
			catch ( FileNotFoundException e ) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public interface ImageCallback {
		public void imageLoaded( Drawable imageDrawable, String imageUrl );
	}
}