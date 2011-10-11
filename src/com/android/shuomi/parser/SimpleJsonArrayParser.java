package com.android.shuomi.parser;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class SimpleJsonArrayParser {
	
	private String[] mItems = null;
	
	public SimpleJsonArrayParser( String inputStream, String key ) {
		parse( inputStream, key );
	}
	
	private void parse( String inputStream, String key ) {
		try {
			JSONArray jsonArray = new JSONArray( inputStream );
			
			if ( jsonArray != null ) {
				Log.d( "ResponseParser", String.valueOf( jsonArray.length() ) );
				mItems = getStringArray( jsonArray, key );
			}
		} 
		catch ( JSONException e ) {
			Log.e( "ResponseParser", e.getMessage() );
		}
	}
	
	private String[] getStringArray( JSONArray jsonArray, String key ) {
		int length = jsonArray.length();
		String[] items = ( length > 0 ) ? new String[length] : null;
		
		for ( int i = 0; i < length; i ++ ) {
			try {
				items[i] = jsonArray.getJSONObject(i).getString( key );
			} 
			catch ( JSONException e ) {
				Log.e( "ResponseParser", e.getMessage() );
				items = null; 
			}
		}
		
		return items;
	}
	
	public String[] getItems() {
		return mItems;
	}
}
