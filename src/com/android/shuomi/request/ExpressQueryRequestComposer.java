package com.android.shuomi.request;

import com.android.shuomi.intent.ExpressQueryRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;

public class ExpressQueryRequestComposer extends RequestComposer {

	protected ExpressQueryRequestComposer(RequestIntent request) {
		super(request);
	}

	@Override
	protected String encodeUri( RequestIntent request ) {
		String url = null;
		
		if ( request.getAction().equals( REQUEST.EXPRESS_QUERY ) ) 
		{
			ExpressQueryRequestIntent req = ( ExpressQueryRequestIntent ) request;
			url = getExpressPrefix() + REQUEST.PARAM_DETAIL;
			url += encodeParam( new String[] { REQUEST.PARAM_CODE, REQUEST.PARAM_NO }, 
					new String[] { req.getExpressId(), req.getOrderNo() } );
		}
		
		return url;
	}
}
