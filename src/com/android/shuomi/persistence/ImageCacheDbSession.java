package com.android.shuomi.persistence;

import android.util.Log;

import com.android.shuomi.util.Util;

public class ImageCacheDbSession extends DatabaseSession {

	static final private String TAG = "ImageCacheDbSession";
	static final private String TABLE_IMAGECACHE = "TABLE_IMAGECACHE";
	static final private String URI = "URI";
	static final private String FILE_NAME = "FILE_NAME";
	static final private String IMAGECACHE_COLUMNS[] = { URI, FILE_NAME };
	static final private int IMAGECACHE_MAX_ROW = 100;
	
	static private ImageCacheDbSession mThis = null;
	
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
		return mDatabase.findRecordByValue( TABLE_IMAGECACHE, new String[]{ FILE_NAME }, URI, uri );
	}
	
	public int getExceedingCount()
	{
		Log.w( TAG, "record count: " + mDatabase.getRecordCount( TABLE_IMAGECACHE ) );
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
		return mDatabase.deleteRecord( TABLE_IMAGECACHE, FILE_NAME, file );
	}
	
	public String[] getOldestRecords( int count )
	{
		return mDatabase.getOldestRecords( TABLE_IMAGECACHE, FILE_NAME, count );
	}
}
