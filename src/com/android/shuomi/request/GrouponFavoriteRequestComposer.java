package com.android.shuomi.request;

import com.android.shuomi.intent.GrouponFavoriteRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;

public class GrouponFavoriteRequestComposer extends RequestComposer {

	protected GrouponFavoriteRequestComposer( RequestIntent request ) {
		super( request );
	}
	
	@Override
	protected String encodeUri( RequestIntent request ) {
		String url = null;
		
		if ( request.getAction().equals( REQUEST.ADD_TO_FAVORITE ) ) {
			GrouponFavoriteRequestIntent req = ( GrouponFavoriteRequestIntent ) request;
			
			url = getCommonPrefix() + REQUEST.PARAM_FAVORITE;
			
			String[] paramKeys = { REQUEST.PARAM_ID };
			String[] paramValues = { req.getItemId() };
			
			url += encodeParam( paramKeys, paramValues );
		}
		
		return url;
	}

}
