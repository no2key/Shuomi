package com.android.shuomi.request;

import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.util.Util;

public class CityRequestComposer extends RequestComposer {

	public CityRequestComposer( RequestIntent request ) {
		super( request );
	}
	
	@Override
	protected String encodeUri( RequestIntent request ) {
		String url = null;
		
		if ( request.getAction().equals( REQUEST.LIST_CITY ) ) {
			url = getCommonPrefix() + REQUEST.PARAM_CITY;
			
			if ( Util.isValid( request.getParam1() ) ) {
				url += encodeParam( new String[]{ REQUEST.PARAM_PROVINCE }, new String[] { request.getParam1() } );
			}
		}
		
		return url;
	}
}
