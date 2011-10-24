package com.android.shuomi.persistence;

import java.util.ArrayList;
import java.util.Observer;

import com.android.shuomi.util.Util;

import android.content.Context;
import android.util.Log;

public class DatabaseSession {

	static final private String TAG = "DatabaseSessionEx";
	static private DatabaseSession mThis = null;
	
	static public DatabaseSession create( Context context ) {
		if ( mThis == null ) {
			mThis = new DatabaseSession( context );
		}
		
		return mThis;
	}
	
	static public DatabaseSession getInstance() {
		if ( mThis == null ) {
			Log.e( TAG, "null database session instance" );
		}
		
		return mThis;
	}	
	
	private ServiceDb mDatabase = null;
	static final private String TABLE_FAVORITE = "TAB_FAVORITE";
	
	public DatabaseSession( Context context ) {
		mDatabase = new ServiceDb( context );
	}
	
	public boolean saveFavoriteRecord( String[] columns, String[] values ) {
		boolean result = false;
		
		if ( Util.isValid( columns ) && Util.isValid( values ) && columns.length == values.length ) {
			result = mDatabase.insertRecord( TABLE_FAVORITE, columns, values );
		}
		
		return result;
	}
	
	public ArrayList<String[]> loadFavoriteRecords( String[] columns, String sortByColumn ) {
		return mDatabase.loadRecords( TABLE_FAVORITE, columns, sortByColumn );
	}
	
	public void registerRecordAddObserver( Observer observer ) {
		mDatabase.registerRecordAddedObserver( observer );
	}
}
