package com.android.shuomi.parser;

import com.android.shuomi.intent.RESPONSE;

public class ExpressQueryResponseParser extends ResponseParser {

	private String[] mKeys = { RESPONSE.PARAM_RETURN };
	
	public ExpressQueryResponseParser( String inputStream ) {
		parse( inputStream );
	}
	
	private void parse( String inputStream ) {
		SimpleJsonParser parser = new SimpleJsonParser( inputStream, mKeys );
		mItems = parser.getValues();
		
		if ( mItems != null ) {
			mType = TYPE_ARRAY;
		}
	}
}
