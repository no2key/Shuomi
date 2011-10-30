package com.android.shuomi.parser;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.util.Util;

public class GrouponAroundResponseParser extends ResponseParser {

	private final String[] mKeys = { 
			RESPONSE.PARAM_IMG_2,  RESPONSE.PARAM_IMG_1, RESPONSE.PARAM_PROVIDER,RESPONSE.PARAM_PRICE, 
			RESPONSE.PARAM_ACTUAL_PRICE, RESPONSE.PARAM_TITLE, RESPONSE.PARAM_EXPIRY, 
			RESPONSE.PARAM_FOLLOWED, RESPONSE.PARAM_ID, RESPONSE.PARAM_URL, RESPONSE.PARAM_CATE,
			RESPONSE.PARAM_NAME, RESPONSE.PARAM_DISTANCE, RESPONSE.PARAM_LONGITUDE, RESPONSE.PARAM_LATITUDE };
	
	public GrouponAroundResponseParser( String inputStream ) {
		parse( inputStream );
	}
	
	private void parse( String inputStream ) {
		try 
		{
			JSONObject object = new JSONObject( inputStream );
			String list = object.getString( RESPONSE.PARAM_LIST );
			
			if ( Util.isValid( list ) )
			{
				JsonArrayParser parser = new JsonArrayParser( list, mKeys );
				ArrayList<String[]> items = parser.getDataList();
				
				if ( items != null )
				{
					mItemsList =  items;
					mType = TYPE_ARRAY_LIST;
				}
			}
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
