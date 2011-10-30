package com.android.shuomi.request;

import com.android.shuomi.intent.GrouponAroundRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;

public class GrouponAroundRequestComposer extends RequestComposer {
	
	public GrouponAroundRequestComposer(RequestIntent request) {
		super(request);
	}

	@Override
	protected String encodeUri( RequestIntent request ) {
		String url = null;
		
		if ( request.getAction().equals( REQUEST.AROUND_GROUPON ) ) 
		{
			GrouponAroundRequestIntent req = ( GrouponAroundRequestIntent ) request;
			url = getCommonPrefix() + REQUEST.PARAM_SHOPLIST;
			url += encodeParam( new String[] { REQUEST.PARAM_LONGITUDE, REQUEST.PARAM_LATITUDE, REQUEST.PARAM_DISTANCE }, 
					new String[] { req.getLongitude(), req.getLatitude(), req.getDistance() } );
		}
		
		return url;
	}
}
