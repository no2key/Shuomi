package com.android.shuomi.request;

import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.util.Util;

public class RequestComposer {

	static private final String URI_CONNECTOR = "?";
	static private final String OP_EQUAL = "=";
	static private final String PARAM_CONNECTOR = "&";
	
	static private final String mBaseUri = "http://www.stylelink.cn/servlet/tuan.jsp";	
	static private final String mOpKey = "go";
	
	static private final String mWeatherUri = "http://www.stylelink.cn/servlet/weather.jsp";
	
	protected String mRequestUri = null;
	
	protected RequestComposer( RequestIntent request ) {
		if ( request != null && Util.isValid( request.getAction() ) ) {
			mRequestUri = encodeUri( request );
		}
	}
	
	protected String encodeUri( RequestIntent request ) {
		return null;
	}
	
	protected String getCommonPrefix() {
		return mBaseUri + URI_CONNECTOR + mOpKey + OP_EQUAL;
	}
	
	protected String getWeatherPrefix() {
		return mWeatherUri + URI_CONNECTOR + mOpKey + OP_EQUAL;
	}
	
	protected String encodeParam( String[] keys, String[] values ) {
		String param = "";
		
		if ( Util.isValid( keys ) && Util.isValid( values ) && keys.length <= values.length ) {
			for ( int i = 0; i < keys.length; i ++ ) {
				//String value = ( values[i] != null ) ? values[i] : "";
				//param += PARAM_CONNECTOR + keys[i] + OP_EQUAL + value;
				if ( Util.isValid( values[i] ) ) {
					param += PARAM_CONNECTOR + keys[i] + OP_EQUAL + values[i];
				}
			}
		}
		
		return param;
	}
	
	public String getRequetUri() {
		return mRequestUri;
	}
}
