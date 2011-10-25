package com.android.shuomi.parser;

import android.util.Log;

import com.android.shuomi.util.Util;

public class WeatherDetailsResponseParser extends ResponseParser {

	private String mRootKey = "weatherinfo";
	private String[] mKeys = { "city", "date_y", "week", "wind1", "index_d",
			"temp1", "temp2", "temp3", "temp4", "temp5",
			"weather1", "weather2", "weather3", "weather4", "weather5", };
	
	public WeatherDetailsResponseParser( String inputStream ) {
		parse( inputStream );
	}
	
	private void parse( String inputStream ) 
	{
		SimpleJsonParser parser = new SimpleJsonParser( inputStream, new String[] { mRootKey } );
		String[] weatherInfo = parser.getValues();
		
		if ( Util.isValid( weatherInfo ) ) 
		{
			SimpleJsonParser inforParser = new SimpleJsonParser( weatherInfo[0], mKeys );
			String[] items = inforParser.getValues();
			
			if ( Util.isValid( items ) ) 
			{
				mItems = items;
				mType = TYPE_ARRAY;
			}
			
			for ( String item : items ) 
			{
				Log.d( "WeatherDetailsResponseParser", item );
			}
		}
	}
}
