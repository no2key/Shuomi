package com.android.shuomi.request;

import com.android.shuomi.intent.GrouponDetailsRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;

public class GrouponDetailsRequestComposer extends RequestComposer {

	protected GrouponDetailsRequestComposer( RequestIntent request ) {
		super(request);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String encodeUri( RequestIntent request ) {
		String url = null;
		
		if ( request.getAction().equals( REQUEST.OBTAIN_DETAILS ) ) 
		{
			GrouponDetailsRequestIntent req = ( GrouponDetailsRequestIntent ) request;
			url = getCommonPrefix() + REQUEST.PARAM_DETAIL;			
			url += encodeParam( new String[] { REQUEST.PARAM_ID }, new String[] { req.getItemId() } );
		}
		
		return url;
	}
}
