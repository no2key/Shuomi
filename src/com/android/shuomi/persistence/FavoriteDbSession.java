package com.android.shuomi.persistence;

import java.util.ArrayList;
import java.util.Observer;

import com.android.shuomi.util.Util;

import android.content.Context;

public class FavoriteDbSession extends DatabaseSession {

	static final private String TABLE_FAVORITE = "TAB_FAVORITE";
	
	public FavoriteDbSession(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
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
	
	public boolean deleteFavoriteRecord( String column, String value ) {
		return mDatabase.deleteRecord( TABLE_FAVORITE, column, value );
	}
	
	public boolean isRecordExisted( String column, String value ) {
		return mDatabase.isRecordExisted( TABLE_FAVORITE , column, value );
	}
}
