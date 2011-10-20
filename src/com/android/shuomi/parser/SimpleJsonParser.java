package com.android.shuomi.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.shuomi.util.Util;

import android.util.Log;

public class SimpleJsonParser {

	private String[] mValues = null;
	
	public SimpleJsonParser( String inputStream, String[] keys ) {
		parse( inputStream, keys );
	}
	
	private void parse( String inputStream, String[] keys ) {
		try {
			JSONObject jsonObj = new JSONObject( inputStream );
			
			if ( jsonObj != null ) {
				Log.d( "SimpleJsonParser", String.valueOf( jsonObj.length() ) );
				mValues = getStringArray( jsonObj, keys );
			}
		} 
		catch ( JSONException e ) {
			Log.e( "ResponseParser", e.getMessage() );
		}
	}

	private String[] getStringArray(JSONObject jsonObj, String[] keys) {
		String[] values = null;
		
		if ( Util.isValid( keys ) && jsonObj != null ) {
			values = new String[ keys.length ];
			for ( int i = 0; i < keys.length; i ++ ) {
				try {
					values[i] = jsonObj.getString( keys[i] );
				} 
				catch ( JSONException e ) {
					values[i] = null;
					Log.e( "SimpleJsonParser", "can NOT find expected key: " + keys[i] );
				}
			}
		}

		return values;
	}
	
	public String[] getValues() {
		return mValues;
	}
}
