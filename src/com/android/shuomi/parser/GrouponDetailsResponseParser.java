package com.android.shuomi.parser;

import com.android.shuomi.intent.RESPONSE;

public class GrouponDetailsResponseParser extends ResponseParser {
	
	private final String[] mKeys = { 
			RESPONSE.PARAM_IMG_2, RESPONSE.PARAM_PROVIDER,RESPONSE.PARAM_PRICE, 
			RESPONSE.PARAM_ACTUAL_PRICE, RESPONSE.PARAM_TITLE, RESPONSE.PARAM_EXPIRY, 
			RESPONSE.PARAM_FOLLOWED, RESPONSE.PARAM_ID, RESPONSE.PARAM_URL, 
			RESPONSE.PARAM_SHOPLIST };
	
	public GrouponDetailsResponseParser( String inputStream ) {
		parse( inputStream );
	}
	
	private void parse( String inputStream ) {
		SimpleJsonParser parser = new SimpleJsonParser( inputStream, mKeys );
		mItems = parser.getValues();
		mType = TYPE_ARRAY;
	}
}
