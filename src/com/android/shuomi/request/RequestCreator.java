package com.android.shuomi.request;

import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RequestIntent;

public class RequestCreator {

	static public RequestComposer create( RequestIntent request ) {
		RequestComposer composer = null;
		String action = request.getAction();
		
		if ( action.equals( REQUEST.LIST_PROVINCE ) ) {
			composer = new ProvinceRequestComposer( request );
		}
		else if ( action.equals( REQUEST.LIST_CITY ) ) {
			composer = new CityRequestComposer( request );
		}
		else if ( action.equals( REQUEST.LIST_GROUPON ) ) {
			composer = new GrouponListRequestComposer( request );
		}
		else if ( action.equals( REQUEST.OBTAIN_DETAILS ) ) {
			composer = new GrouponDetailsRequestComposer( request );
		}
		else if ( action.equals( REQUEST.SHARED_BY_EMAIL ) ) {
			composer = new GrouponSharedByEmailRequestComposer( request );
		}
		else if ( action.equals( REQUEST.ADD_TO_FAVORITE ) ) {
			composer = new GrouponFavoriteRequestComposer( request );
		}
		else if ( action.equals( REQUEST.WEATHER_PROVINCE ) ) {
			composer = new WeatherProvinceRequestComposer( request );
		}
		else if ( action.equals( REQUEST.WEATHER_CITY ) ) {
			composer = new WeatherCityRequestComposer( request );
		}
		else if ( action.equals( REQUEST.WEATHER_COUNTY ) ) {
			composer = new WeatherCountyRequestComposer( request );
		}
		else if ( action.equals( REQUEST.WEATHER_DETAILS ) ) {
			composer = new WeatherDetailsRequestComposer( request );
		}
		else if ( action.equals( REQUEST.EXPRESS_LIST ) ) {
			composer = new ExpressListRequestComposer( request );
		}
		else if ( action.equals( REQUEST.EXPRESS_QUERY ) ) {
			composer = new ExpressQueryRequestComposer( request );
		}
		else if ( action.equals( REQUEST.AROUND_GROUPON ) ) {
			composer = new GrouponAroundRequestComposer( request );
		}
		
		return composer;
	}
}
