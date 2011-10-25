package com.android.shuomi.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.shuomi.NetworkRequestLayout;
import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.persistence.Preference;
import com.android.shuomi.util.Util;

public class WeatherServiceView extends NetworkRequestLayout {

	private static final String DEF_LOCATION_ID = "101010100";
	
	public WeatherServiceView( Context context ) {
		super(context);

		inflateLayout();
		requestList( loadLocationId() );
	}
	
	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.weather_service_view, this, true );
		TextView title = (TextView) findViewById( R.id.titlebar_label );
		title.setText( R.string.weather_service );
		Button button = (Button) findViewById( R.id.titlebar_button );
		button.setText( R.string.select_city );
		
		button.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				( ( ServiceListView ) getContext() ).goToNextView( new WeatherProvinceListView( getContext() ) );
			}
		});
	}
	
	private String loadLocationId() {
		SharedPreferences pref = getContext().getSharedPreferences ( "groupon", 0 );
		return pref.getString( Preference.WEATHER_LOCATION_ID , DEF_LOCATION_ID );
	}
	
	private void requestList( String locationId ) {
		RequestIntent request = new RequestIntent( REQUEST.WEATHER_DETAILS );
		request.setResponseAction( RESPONSE.HTTP_RESPONSE_WEATHER );
		request.setSourceClass( getClass().getName() );
		
		if ( Util.isValid( locationId ) ) {
			request.setParam1( locationId );
		}
		
		sendRequest( request );
	}

	@Override
	public void update( String requestAction, int dataType, Object data, int pageCount ) {
		Log.d( "WeatherServiceView", "update weather details" );
		
	}
	
	public void setLocationId( String locationId ) {
		
	}

}
