package com.android.shuomi.request;

import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;

public class ProvinceRequestComposer extends RequestComposer {

	static private final String mValueProvince = "province";
	
	public ProvinceRequestComposer( RequestIntent request ) {
		super( request );
	}

	protected String encodeUri( RequestIntent request ) {
		String url = null;
		
		if ( request.getAction().equals( REQUEST.LIST_PROVINCE ) ) {
			url =  getCommonPrefix() + mValueProvince;
		}
		
		return url;
	}
}
