package com.android.shuomi.groupon;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.android.shuomi.persistence.ImageCache;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AsyncImageLoader 
{
	private enum STORAGE 
	{
		NONE,
		CACHE,
		FILE,
	}
	
	private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	
	public Drawable loadDrawable( final Context context, final String imageUrl, final ImageCallback callback ) 
	{
		return loadDrawable( context, imageUrl, null, callback, STORAGE.NONE );
	}
	
	public Drawable loadDrawableWithCache( final Context context, final String imageUrl, final ImageCallback callback ) 
	{
		return loadDrawable( context, imageUrl, null, callback, STORAGE.CACHE );
	}
	
	public Drawable loadDrawableWithStore( final Context context, final String imageUrl, final String fileName, final ImageCallback callback ) 
	{
		return loadDrawable( context, imageUrl, fileName, callback, STORAGE.FILE );
	}
	
	private Drawable loadDrawable( final Context context, final String imageUrl, final String fileName, final ImageCallback callback, final STORAGE type ) 
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
				Drawable drawable = null;
				
				if ( type == STORAGE.NONE )
				{
					drawable = loadImage( context, imageUrl );
				}
				else if ( type == STORAGE.CACHE )
				{
					drawable = loadImageWithCache( context, imageUrl );
				}
				else if ( type == STORAGE.FILE )
				{
					drawable = loadImage( context, imageUrl, fileName );
				}
				
				if ( drawable != null ) 
				{
					imageCache.put( imageUrl, new SoftReference<Drawable>( drawable ) );
				}
				
				handler.sendMessage( handler.obtainMessage( 0, drawable ) );
			};
		}.start();
		
		return null;
	}
	
	private Drawable loadImage( Context context, String imageUrl )
	{
		Drawable image = null;
		
		try 
		{
			image = Drawable.createFromStream( new URL( imageUrl ).openStream(), "src" );
		}
		catch ( Exception e ) 
		{
			Log.e( "AsyncImageLoader", "loadImageFromUrl: " + e.getMessage() );
		}
		
		return image;
	}
	
	private Drawable loadImage( Context context, String imageUrl, String fileName )
	{
		Drawable image = null;
		
		try 
		{
			if ( ImageCache.saveImageFile( context, new URL( imageUrl ).openStream(), fileName ) )
			{
				File imageFile = context.getFileStreamPath( fileName );
				
				if ( imageFile.exists() )
				{
					image = Drawable.createFromPath( imageFile.getAbsolutePath() );
				}
			}
		}
		catch ( Exception e ) 
		{
			Log.e( "AsyncImageLoader", "loadImageFromUrl: " + e.getMessage() );
		}
		
		return image;
	}
	
	private Drawable loadImageWithCache( Context context, String imageUrl )
	{
		Drawable image = null;
		
		try 
		{
			image = ImageCache.loadImage( context, imageUrl );
			
			if ( image == null )
			{
				InputStream imageStream = new URL( imageUrl ).openStream();			
				String file = ImageCache.saveImage( context, imageStream, imageUrl );
				
				if ( Util.isValid( file ) )
				{
					File imageFile = context.getFileStreamPath( file );
					image = Drawable.createFromPath( imageFile.getAbsolutePath() );
					Log.i( "AsyncImageLoader", "image from path: " + imageFile.getAbsolutePath() );
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