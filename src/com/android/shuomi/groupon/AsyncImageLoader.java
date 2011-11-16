package com.android.shuomi.groupon;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.android.shuomi.util.Util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AsyncImageLoader 
{
	private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	
	public Drawable loadDrawableWithoutCache( final Context context, final String imageUrl, final ImageCallback callback ) 
	{
		return loadDrawable( context, imageUrl, callback, false );
	}
	
	public Drawable loadDrawableWithCache( final Context context, final String imageUrl, final ImageCallback callback ) 
	{
		return loadDrawable( context, imageUrl, callback, true );
	}
	
	public Drawable loadDrawableWithStore( final Context context, final String imageUrl, final ImageCallback callback, String fileName ) 
	{
		return loadDrawable( context, imageUrl, callback, true );
	}
	
	private Drawable loadDrawable( final Context context, final String imageUrl, final ImageCallback callback, final boolean withCache ) 
	{
		if ( imageCache.containsKey( imageUrl ) ) 
		{
			SoftReference<Drawable> softReference = imageCache.get( imageUrl );
			
			if ( softReference.get() != null ) 
			{
				return softReference.get();
			}
		}
		
		final Handler handler = new Handler() 
		{
			@Override
			public void handleMessage( Message msg ) 
			{
				if ( (Drawable) msg.obj != null ) 
				{
					callback.imageLoaded( (Drawable) msg.obj, imageUrl );
				}
			}
		};
		
		new Thread() 
		{
			public void run() 
			{
				Drawable drawable = loadImageFromUrl( context, imageUrl, withCache );
				
				if ( drawable != null ) 
				{
					imageCache.put( imageUrl, new SoftReference<Drawable>( drawable ) );
				}
				
				handler.sendMessage( handler.obtainMessage( 0, drawable ) );
			};
		}.start();
		
		return null;
	}
	
	private Drawable loadImageFromUrl( Context context, String imageUrl, boolean withCache ) 
	{
		Drawable image = null;
		
		try 
		{
			if ( withCache )
			{
				image = ImageCache.loadImage( context, imageUrl );
			}
			
			if ( image == null )
			{
				InputStream imageStream = new URL( imageUrl ).openStream();
				
				if ( withCache )
				{
					String file = ImageCache.saveImage( context, imageStream, imageUrl );
					
					if ( Util.isValid( file ) )
					{
						File imageFile = context.getFileStreamPath( file );
						image = Drawable.createFromPath( imageFile.getAbsolutePath() );
						Log.i( "AsyncImageLoader", "image from path: " + imageFile.getAbsolutePath() );
					}
				}
				else
				{
					image = Drawable.createFromStream( imageStream, "src" );
				}
			}
		}
		catch ( Exception e ) 
		{
			Log.e( "AsyncImageLoader", "loadImageFromUrl: " + e.getMessage() );
		}
		
		return image;
	}

	public interface ImageCallback 
	{
		public void imageLoaded( Drawable imageDrawable, String imageUrl );
	}
}