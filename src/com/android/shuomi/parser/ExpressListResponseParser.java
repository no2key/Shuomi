package com.android.shuomi.parser;

public class ExpressListResponseParser extends ResponseParser {

	private String[] mKeys = { "code", "name" };
	
	public ExpressListResponseParser( String inputStream ) {
		parse( inputStream );
	}
	
	private void parse( String inputStream ) {
		JsonArrayParser parser = new JsonArrayParser( inputStream, mKeys );
		mItemsList = parser.getDataList();
		
		if ( mItemsList != null ) {
			mType = TYPE_ARRAY_LIST;
		}
	}
}
