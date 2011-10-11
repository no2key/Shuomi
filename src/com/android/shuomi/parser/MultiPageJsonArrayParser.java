package com.android.shuomi.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.util.Util;

public class MultiPageJsonArrayParser {

	private int mPage = 0;
	private ArrayList<String[]> mList = null;
	
	public MultiPageJsonArrayParser( String inputStream, String[] keys ) {
		try {
			JSONObject obj = new JSONObject( inputStream );
			mPage = obj.getInt( RESPONSE.PARAM_PAGES );
			JSONArray list = obj.getJSONArray( RESPONSE.PARAM_LIST );
			if ( list != null && Util.isValid( keys ) ) {
				getArrayList( list, keys );
			}
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getArrayList( JSONArray list, String[] keys ) {
		mList = new ArrayList<String[]>();
		
		for ( int i = 0; i < list.length(); i ++ ) {
			String[] values = new String[keys.length];
			
			for ( int j = 0; j < keys.length; j ++ ) {
				try {
					values[j] = list.getJSONObject(i).getString( keys[j] );
				} 
				catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mList.add( values );
		}
	}
	
	public int getPageCount() {
		return mPage;
	}
	
	public ArrayList<String[]> getArryList() {
		return mList;
	}
}
