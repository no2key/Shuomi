package com.android.shuomi.parser;

import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.ResponseIntent;

public class ResponseParserCreator {
	
	static public ResponseParser create( ResponseIntent response ) {
		ResponseParser parser = null;
		String requestAction = response.getRequestAction();
		
		if ( requestAction.equals( REQUEST.LIST_GROUPON ) ) {
			parser = new GrouponListResponseParser( response.getUserData() );
		}
		else if ( requestAction.equals( REQUEST.OBTAIN_DETAILS ) ) {
			parser = new GrouponDetailsResponseParser( response.getUserData() );
		}
		else if ( requestAction.equals( REQUEST.WEATHER_PROVINCE ) || 
				  requestAction.equals( REQUEST.WEATHER_CITY ) || 
				  requestAction.equals( REQUEST.WEATHER_COUNTY ) ) {
			parser = new WeatherLocationResponseParser( response.getUserData() );
		}
		else if ( requestAction.equals( REQUEST.WEATHER_DETAILS ) ) {
			parser = new WeatherDetailsResponseParser( response.getUserData() );
		}
		else if ( requestAction.equals( REQUEST.EXPRESS_LIST ) ) {
			parser = new ExpressListResponseParser( response.getUserData() );
		}
		else if ( requestAction.equals( REQUEST.EXPRESS_QUERY ) ) {
			parser = new ExpressQueryResponseParser( response.getUserData() );
		}
		else {
			parser = new ResponseParser( response.getUserData(), requestAction );
		}
		
		return parser;
	}
}
