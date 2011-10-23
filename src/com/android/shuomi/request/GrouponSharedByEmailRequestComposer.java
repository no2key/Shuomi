package com.android.shuomi.request;

import com.android.shuomi.intent.GrouponSharedByEmailRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;

public class GrouponSharedByEmailRequestComposer extends RequestComposer {

	public GrouponSharedByEmailRequestComposer( RequestIntent request ) {
		super(request);
	}

	@Override
	protected String encodeUri( RequestIntent request ) {
		String url = null;
		
		if ( request.getAction().equals( REQUEST.SHARED_BY_EMAIL ) ) {
			GrouponSharedByEmailRequestIntent req = ( GrouponSharedByEmailRequestIntent ) request;
			
			url = getCommonPrefix() + REQUEST.PARAM_SHARE;
			
			String[] paramKeys = { REQUEST.PARAM_ID, REQUEST.PARAM_EMAIL };
			String[] paramValues = { req.getItemId(), req.getEmailAddress() };
			
			url += encodeParam( paramKeys, paramValues );
		}
		
		return url;
	}
}
