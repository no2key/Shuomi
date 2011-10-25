package com.android.shuomi.request;

import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;

public class WeatherProvinceRequestComposer extends RequestComposer {

	static private final String mValueProvince = "list1";
	
	public WeatherProvinceRequestComposer( RequestIntent request ) {
		super( request );
	}

	protected String encodeUri( RequestIntent request ) {
		String url = null;
		
		if ( request.getAction().equals( REQUEST.WEATHER_PROVINCE ) ) {
			url =  getWeatherPrefix() + mValueProvince;
		}
		
		return url;
	}
}
