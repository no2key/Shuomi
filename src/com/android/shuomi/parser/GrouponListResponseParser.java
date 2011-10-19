package com.android.shuomi.parser;

import com.android.shuomi.intent.RESPONSE;

public class GrouponListResponseParser extends ResponseParser {

	private final String[] mKeys = { RESPONSE.PARAM_IMG_1, RESPONSE.PARAM_PROVIDER, 
			RESPONSE.PARAM_PRICE, RESPONSE.PARAM_ACTUAL_PRICE, RESPONSE.PARAM_TITLE, RESPONSE.PARAM_ID };
	
	public GrouponListResponseParser( String inputStream ) {
		parse( inputStream );
	}
	
	private void parse( String inputStream ) {
		MultiPageJsonArrayParser parser = new MultiPageJsonArrayParser( inputStream, mKeys );
		mItemsList = parser.getArryList();
		mPage = parser.getPageCount();
		mType = TYPE_ARRAY_LIST;
	}
}
