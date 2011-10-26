package com.android.shuomi.request;

import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;

public class ExpressListRequestComposer extends RequestComposer {
	
	public ExpressListRequestComposer( RequestIntent request ) {
		super(request);
	}

	@Override
	protected String encodeUri( RequestIntent request ) {
		String url = null;
		
		if ( request.getAction().equals( REQUEST.EXPRESS_LIST ) ) 
		{
			url = getExpressPrefix() + REQUEST.PARAM_LIST;
		}
		
		return url;
	}
}
