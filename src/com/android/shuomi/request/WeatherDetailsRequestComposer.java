package com.android.shuomi.request;

import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.util.Util;

public class WeatherDetailsRequestComposer extends RequestComposer {

	static private final String mValueDetails = "detail";
	
	public WeatherDetailsRequestComposer( RequestIntent request ) {
		super( request );
	}

	protected String encodeUri( RequestIntent request ) {
		String url = null;
		
		if ( request.getAction().equals( REQUEST.WEATHER_DETAILS ) ) {
			url =  getWeatherPrefix() + mValueDetails;
			
			if ( Util.isValid( request.getParam1() ) ) {
				url += encodeParam( new String[]{ REQUEST.PARAM_NO }, new String[] { request.getParam1() } );
			}
		}
		
		return url;
	}
}
