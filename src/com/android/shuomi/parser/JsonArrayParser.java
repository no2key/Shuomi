package com.android.shuomi.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.shuomi.util.Util;

public class JsonArrayParser {
	
	private ArrayList<String[]> mList = null;
	
	public JsonArrayParser( String inputStream, String[] keys ) {
		try {
			JSONArray jsonArray = new JSONArray( inputStream );
			parse( jsonArray, keys );
		} 
		catch ( JSONException e ) {
			e.printStackTrace();
		}
	}
	
	private void parse( JSONArray jsonArray, String[] keys ) {
		if ( jsonArray != null && jsonArray.length() > 0 && Util.isValid( keys ) ) {
			mList = new ArrayList<String[]> ();
			
			for ( int i = 0; i < jsonArray.length(); i ++ ) {
				try {
					JSONObject object = jsonArray.getJSONObject( i );
					String items[] = getItems( object, keys );
					mList.add( items );
				} 
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String[] getItems( JSONObject object, String[] keys ) {
		String[] items = null;
		
		if ( object != null && Util.isValid( keys ) && object.length() >= keys.length ) {
			items = new String[keys.length];
			
			for ( int i = 0; i < keys.length; i ++ ) {
				try {
					items[i] = object.getString( keys[i] );
				} 
				catch (JSONException e) {
					items[i] = null;
				}
			}
		}
		return items;
	}
	
	public ArrayList<String[]> getDataList() {
		return mList;
	}
}
