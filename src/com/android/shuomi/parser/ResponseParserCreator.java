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
		else {
			parser = new ResponseParser( response.getUserData(), requestAction );
		}
		
		return parser;
	}
}
