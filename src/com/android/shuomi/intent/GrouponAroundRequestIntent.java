package com.android.shuomi.intent;

public class GrouponAroundRequestIntent extends RequestIntent {

	public GrouponAroundRequestIntent( String className, int latitude, int longitude, int distance  ) {
		super( REQUEST.AROUND_GROUPON );
		setSourceClass( className );
		setResponseAction( RESPONSE.HTTP_RESPONSE_AROUND );
		
		setLatitude( latitude );
		setLongitude( longitude );
		setDistance( distance );
	}

	public void setLatitude( int latitude ) {
		float x = (float) ((float)latitude / 1E6);
		putExtra( REQUEST.PARAM_LATITUDE, String.valueOf( x ) );
	}
	
	public String getLatitude() {
		return getStringExtra( REQUEST.PARAM_LATITUDE );
	}
	
	public void setLongitude( int longitude ) {
		float y = (float) ((float)longitude / 1E6);
		putExtra( REQUEST.PARAM_LONGITUDE, String.valueOf( y ) );
	}
	
	public String getLongitude() {
		return getStringExtra( REQUEST.PARAM_LONGITUDE );
	}
	
	public void setDistance( int distance ) {
		putExtra( REQUEST.PARAM_DISTANCE, String.valueOf( distance ) );
	}
	
	public String getDistance() {
		return getStringExtra( REQUEST.PARAM_DISTANCE );
	}
}
