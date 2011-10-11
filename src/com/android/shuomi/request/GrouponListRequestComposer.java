package com.android.shuomi.request;

import com.android.shuomi.intent.GrouponListRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;

public class GrouponListRequestComposer extends RequestComposer {

	public GrouponListRequestComposer( RequestIntent request ) {
		super( request );
	}
	
	@Override
	protected String encodeUri( RequestIntent request ) {
		String url = null;
		
		if ( request.getAction().equals( REQUEST.LIST_GROUPON ) ) {
			GrouponListRequestIntent req = ( GrouponListRequestIntent ) request;
			url = getCommonPrefix() + REQUEST.PARAM_LIST;
			String[] paramKeys = { REQUEST.PARAM_PROVINCE, REQUEST.PARAM_CITY, REQUEST.PARAM_CATE, 
					REQUEST.PARAM_TITLE, REQUEST.PARAM_PAGE, REQUEST.PARAM_ROW, REQUEST.PARAM_ORDER };
			String[] paramValues = { req.getProvince(), req.getCity(), req.getCate(),
					req.getTitle(), String.valueOf( req.getPage() ), req.getRow(), req.getOrder() };
			url += encodeParam( paramKeys, paramValues );
		}
		
		return url;
	}
}
