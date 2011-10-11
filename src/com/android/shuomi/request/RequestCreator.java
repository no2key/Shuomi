package com.android.shuomi.request;

import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;

public class RequestCreator {

	static public RequestComposer create( RequestIntent request ) {
		RequestComposer composer = null;
		String action = request.getAction();
		
		if ( action.equals( REQUEST.LIST_PROVINCE ) ) {
			composer = new ProvinceRequestComposer( request );
		}
		else if ( action.equals( REQUEST.LIST_CITY ) ) {
			composer = new CityRequestComposer( request );
		}
		else if ( action.equals( REQUEST.LIST_GROUPON ) ) {
			composer = new GrouponListRequestComposer( request );
		}
		
		return composer;
	}
}
