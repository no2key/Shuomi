package com.android.shuomi.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
		layoutInflater.inflate( R.layout.weather_service_view1, this, true );
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
	
	private void switchView() {
		findViewById( R.id.empty_view_loading ).setVisibility( View.GONE );
		findViewById( R.id.content_view ).setVisibility( View.VISIBLE );
	}

	@Override
	public void update( String requestAction, int dataType, Object data, int pageCount ) {
		Log.d( "WeatherServiceView", "update weather details" );
		
		String[] items = ( String[] ) data;
		
		if ( requestAction.equals( REQUEST.WEATHER_DETAILS ) && Util.isValid( items ) ) {
			setupTodayWeather( items[0], items[10], items[3], items[5], items[4] );
			setNextDayWeather( items[2], 1, R.id.day_1, items[11], items[6] );
			setNextDayWeather( items[2], 2, R.id.day_2, items[12], items[7] );
			setNextDayWeather( items[2], 3, R.id.day_3, items[13], items[8] );
			setNextDayWeather( items[2], 4, R.id.day_4, items[14], items[9] );
			setTextView( 0 , R.id.last_updated, getContext().getString( R.string.last_updated ) + items[1] );
		}
		
		switchView();
	}
	
	private void setTextView( int parentId, int resId, String label ) {
		TextView view = null;
		
		if ( parentId == 0 ) 
		{
			view = (TextView) findViewById( resId );
		}
		else
		{
			view = (TextView) findViewById( parentId ).findViewById( resId );
		}
		
		if ( view != null )
		{
			view.setText( label );
		}		
	}
	
	private void setImageView( int parentId, int resId, int imageId ) 
	{
		ImageView view = null;
		
		if ( parentId == 0 ) 
		{
			view = (ImageView) findViewById( resId );
		}
		else
		{
			view = (ImageView) findViewById( parentId ).findViewById( resId );
		}
		
		if ( view != null )
		{
			view.setImageResource( imageId );
		}		
	}
	
	private void setNextDayWeather( String currentWeekday, int offset, int parentViewId, String weather, String temperature ) 
	{
		setTextView( parentViewId, R.id.weekday, getWeekday( currentWeekday, offset ) );
		setTextView( parentViewId, R.id.weather, weather );
		setTextView( parentViewId, R.id.temperature, temperature );
		setImageView( parentViewId, R.id.img_weather, getWeatherDemoImage( weather ) );
	}
	
	private String getWeekday( String weekday, int offset ) {
		String day = "";
		int resIds[] = { R.string.sunday, R.string.monday, R.string.tuesday, R.string.wednesday, 
				         R.string.thursday, R.string.friday, R.string.saturday };
		
		for ( int i = 0; i < resIds.length; i ++ ) 
		{
			String string = getContext().getString( resIds[i] );
			if ( weekday.equals( string ) )
			{
				i = ( i + offset ) % 7;
				day = getContext().getString( resIds[i] );
				break;
			}
		}
		
		return day;
	}
	
	private void setupTodayWeather( String city, String weather, String wind, String temperature, String description ) {
		TextView cityView = (TextView) findViewById( R.id.city );
		cityView.setText( city );
		
		TextView weatherView = (TextView) findViewById( R.id.weather );
		weatherView.setText( weather );
		
		TextView windView = (TextView) findViewById( R.id.wind );
		windView.setText( wind );
		
		TextView descriptionView = (TextView) findViewById( R.id.description );
		descriptionView.setText( description );
		
		TextView temperatureView = (TextView) findViewById( R.id.temperature );
		temperatureView.setText( temperature );
		
		ImageView imgWeather = (ImageView) findViewById( R.id.img_weather );
		imgWeather.setImageResource( getWeatherDemoImage( weather ) );
	}
	
	private boolean containId( String str, int strResId ) {
		return str.contains( getContext().getString( strResId ) );
	}
	
	private int getWeatherDemoImage( String weather ) {
		int resId = R.drawable.fine;
		
		if (  containId( weather, R.string.heary_rain ) ) 
		{
			resId = R.drawable.heary_rain;
		}
		else if ( containId( weather, R.string.fog ) )
		{
			resId = R.drawable.fog;
		}
		else if ( containId( weather, R.string.snow ) )
		{
			resId = R.drawable.snowing;
		}
		else if ( containId( weather, R.string.thunder ) )
		{
			resId = R.drawable.thunder_storm;
		}
		else if ( containId( weather, R.string.middle_rain ) || containId( weather, R.string.light_rain ) ) 
		{
			resId = R.drawable.light_rain;
		}
		else if ( containId( weather, R.string.cloudy ) )
		{
			resId = R.drawable.cloudy;
		}
		else if ( containId( weather, R.string.grey_day ) )
		{
			resId = R.drawable.grey_day;
		}
		else if ( containId( weather, R.string.fine ) && containId( weather, R.string.rain ) )
		{
			resId = R.drawable.fine_rain;
		}
		else if ( containId( weather, R.string.fine ) )
		{
			resId = R.drawable.fine;
		}
		
		return resId;
	}
	
	public void setLocationId( String locationId ) {
		requestList( locationId );
	}

}
