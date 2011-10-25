package com.android.shuomi.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShopListParser extends JsonArrayParser {

	static public String PARAM_NAME = "name";
	static public String PARAM_ADDR = "addr";
	static public String PARAM_TEL = "tel";
	static public String PARAM_X = "x";
	static public String PARAM_Y = "y";	
	
	static private String[] mParams = { PARAM_NAME, PARAM_ADDR, PARAM_TEL, PARAM_X, PARAM_Y };
	
	public ShopListParser( String inputStream ) {
		super( inputStream, mParams );
	}
//	
//	private ArrayList<String[]> mList = null;
//	
//	public ShopListParser( String inputStream ) {
//		try {
//			JSONArray jsonArray = new JSONArray( inputStream );
//			parse( jsonArray );
//		} 
//		catch ( JSONException e ) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void parse( JSONArray jsonArray ) {
//		if ( jsonArray != null && jsonArray.length() > 0 ) {
//			mList = new ArrayList<String[]> ();
//			
//			for ( int i = 0; i < jsonArray.length(); i ++ ) {
//				try {
//					JSONObject object = jsonArray.getJSONObject( i );
//					String items[] = getItems( object );
//					mList.add( items );
//				} 
//				catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//	
//	private String[] getItems( JSONObject object ) {
//		String[] items = null;
//		
//		if ( object != null && object.length() >= mParams.length ) {
//			items = new String[mParams.length];
//			
//			for ( int i = 0; i < mParams.length; i ++ ) {
//				try {
//					items[i] = object.getString( mParams[i] );
//				} 
//				catch (JSONException e) {
//					items[i] = null;
//				}
//			}
//		}
//		return items;
//	}
//	
//	public ArrayList<String[]> getDataList() {
//		return mList;
//	}
}
