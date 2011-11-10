package com.android.shuomi.persistence;

import com.android.shuomi.util.Util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ImageCacheDbSession extends DatabaseSession {

	static final private String TAG = "ImageCacheDbSession";
	static final private String TABLE_IMAGECACHE = "TABLE_IMAGECACHE";
	static final private String IMAGECACHE_COLUMNS[] = { "URI", "FILE_NAME" };
	static final private int IMAGECACHE_MAX_ROW = 100;
	
	static private ImageCacheDbSession mThis = null;
	
//	static public ImageCacheDbSession create() 
//	{
//		if ( mThis == null ) 
//		{
//			mThis = new ImageCacheDbSession();
//		}
//		
//		return mThis;
//	}
	
	static public ImageCacheDbSession getInstance() 
	{
		if ( mThis == null ) 
		{
			DatabaseSession parent = DatabaseSession.getInstance();
			
			if ( parent != null )
			{
				mThis = new ImageCacheDbSession( parent );
			}
		}
		
		return mThis;
	}
	
	private ImageCacheDbSession( DatabaseSession session ) {
		super( session );
	}

	public String getFileName( String uri )
	{
		//Log.w( TAG, "getFileName uri: " + uri );
		//Log.w( TAG, "getFileName mDatabase: " + mDatabase.toString() );
		
		SQLiteDatabase db = mDatabase.getReadableDatabase();
		//Log.w( TAG, "getFileName db: " + db.toString() );
		
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_IMAGECACHE, new String[]{ "FILENAME" }, "URI=?", new String[] { uri }, null, null, null );
		}
		catch( Exception e )
		{
			Log.w( TAG, "getFileName, query error: " + e.getMessage() );
		}
		
		String file = null;
		
		if ( cursor != null && cursor.getCount() > 0 )
		{
			file = cursor.getString( 0 );
		}
		else 
		{
			Log.w( TAG, "get NO record for uri: " + uri );
		}
		
		return file;
	}
	
	public int getExceedingCount()
	{
		return mDatabase.getRecordCount( TABLE_IMAGECACHE ) - IMAGECACHE_MAX_ROW + 1;
	}
	
	public boolean addRecord( String uri, String file )
	{
		boolean result = false;
		
		if ( Util.isValid( uri ) && Util.isValid( file ) )
		{
			result = mDatabase.insertRecord( TABLE_IMAGECACHE, IMAGECACHE_COLUMNS, new String[] { uri, file } );
		}
		
		return result;
	}
	
	public boolean deleteRecord( String file )
	{
		return mDatabase.deleteRecord( TABLE_IMAGECACHE, "FILE_NAME", file );
	}
	
	public String[] getOldestRecords( int count )
	{
		return mDatabase.getOldestRecords( TABLE_IMAGECACHE, "FILE_NAME", count );
	}
}
