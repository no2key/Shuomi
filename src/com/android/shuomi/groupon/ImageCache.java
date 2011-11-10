package com.android.shuomi.groupon;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.android.shuomi.StreamReader;
import com.android.shuomi.persistence.ImageCacheDbSession;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ImageCache {

	static private final String TAG = "ImageCache";
	
	static public Drawable loadImage( Context context, String uri )
	{
		Log.d( TAG, "loadImage start" );
		
		ImageCacheDbSession session = ImageCacheDbSession.getInstance();
		
		String file = ( session != null ) ? session.getFileName( uri ) : null;
		
		if ( Util.isValid( file ) )
		{
			Log.d( TAG, "got file name: " + file + " from db for uri: " + uri );
		}
		else
		{
			Log.w( TAG, "CANNOT got file name from db for uri: " + uri );
		}
		
		return Util.isValid( file ) ? readImageFile( context, file ) : null;
	}
	
	static private Drawable readImageFile( Context context, String file )
	{
		Drawable image = null;
		
		try 
		{
			InputStream imageStream = context.openFileInput( file );
			image = Drawable.createFromStream( imageStream, "src" );
			Log.d( TAG, "got image from file: " + file );
		} 
		catch (FileNotFoundException e) 
		{
			Log.e( TAG, "file not found when reading file: " + file );
		}
		
		return image;
	}
	
	static public String saveImage( Context context, InputStream is, String uri )
	{
		String fileName = null;
		
		if ( checkExceeding( context ) )
		{
			fileName = String.valueOf( System.currentTimeMillis() );
			Log.d( TAG, "saveImage: fileName: " + fileName + ", uri: " + uri );
			
			if ( saveImageFile( context, is, fileName ) )
			{
				Log.d( TAG, "saveImage: ready to write data to db" );
				((ImageCacheDbSession) ImageCacheDbSession.getInstance()).addRecord( uri, fileName );
			}
			else 
			{
				fileName = null;
			}
		}
		
		return fileName;
	}
	
	static private boolean checkExceeding( Context context )
	{
		boolean result = true;
		
		int exceeding = ((ImageCacheDbSession) ImageCacheDbSession.getInstance()).getExceedingCount();			
		
		if ( exceeding > 0 )
		{
			String[] files = ((ImageCacheDbSession) ImageCacheDbSession.getInstance()).getOldestRecords( exceeding );
			purgeRecords( context, files );
		}
		
		return result;
	}
	
	static private boolean purgeRecords( Context context, String[] files )
	{
		boolean result = false;
		
		if ( Util.isValid( files ) )
		{
			for ( String file : files )
			{
				result = purgeRecord( context, file );
				
				if ( !result )
				{
					break;
				}
			}
		}
		
		return result;
	}
	
	static private boolean purgeRecord( Context context, String file )
	{
		boolean result = ((ImageCacheDbSession) ImageCacheDbSession.getInstance()).deleteRecord( file );
		
		if ( result )
		{
			result = context.deleteFile( file );
		}
		
		return result;
	}
	
	static private boolean saveImageFile( Context context, InputStream is, String file )
	{
		boolean result = false;
		StreamReader reader = new StreamReader( is, 1024*8 );
		
		Log.d( TAG, "saveImageFile, size: " + reader.size() );
		
		if ( reader.size() > 0 )
		{
			try 
			{
				FileOutputStream fos = context.openFileOutput( file, Context.MODE_PRIVATE );
				fos.write( reader.getString().getBytes() ); 
				fos.close();
				result = true;
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
		
		return result;
	}
}
