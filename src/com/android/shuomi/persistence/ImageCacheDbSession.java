package com.android.shuomi.persistence;

import com.android.shuomi.util.Util;

import android.content.Context;

public class ImageCacheDbSession extends DatabaseSession {

	static final private String TABLE_IMAGECACHE = "TABLE_IMAGECACHE";
	static final private String IMAGECACHE_COLUMNS[] = { "URI", "FILE_NAME" };
	static final private int IMAGECACHE_MAX_ROW = 100;

	public ImageCacheDbSession(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	public boolean addImageCacheRecord( String uri, String file )
	{
		boolean result = false;
		
		if ( Util.isValid( uri ) && Util.isValid( file ) )
		{
			result = mDatabase.insertRecord( TABLE_IMAGECACHE, IMAGECACHE_COLUMNS, new String[] { uri, file } );
		}
		
		return result;
	}
	
	//public boolean deleteOldestImageCacheRecord
}
