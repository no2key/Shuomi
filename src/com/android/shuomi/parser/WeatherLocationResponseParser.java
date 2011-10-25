package com.android.shuomi.parser;

public class WeatherLocationResponseParser extends ResponseParser {

	private String[] mKeys = { "no", "name" };
	
	public WeatherLocationResponseParser( String inputStream ) {
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
