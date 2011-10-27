package com.android.shuomi.around;

import com.android.shuomi.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class GrouponAroundView extends LinearLayout {

	private MapView mMapView;
	
	public GrouponAroundView( Context context ) {
		super(context);
		inflateLayout();
		goToLocation( getCurrentGeoPoint() );
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.groupon_around, this, true );
		
		initView();
	}
	
	private GeoPoint getCurrentGeoPoint() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );
        String provider = locationManager.getBestProvider( getCriteria(), true );
        Log.d( "GrouponAroundView", "best provide" + provider );
        
        Location location = locationManager.getLastKnownLocation( provider );

        return ( location != null ) 
        		? new GeoPoint( (int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6) ) 
        		: null;
    }
	
	private Criteria getCriteria() 
	{
		Criteria criteria = new Criteria();
        criteria.setAccuracy( Criteria.ACCURACY_FINE );
        criteria.setAltitudeRequired( false );
        criteria.setBearingRequired( false );
        criteria.setCostAllowed( true );
        criteria.setPowerRequirement( Criteria.NO_REQUIREMENT );
        criteria.setSpeedRequired( false );
        
        return criteria;
	}
	
	private void initView()
	{
		mMapView = (MapView) findViewById( R.id.mapview );
		mMapView.setTraffic( false );		
		mMapView.getController().setZoom( 16 );
	}
	
	private void goToLocation( GeoPoint point ) 
	{
		if ( point != null )
		{
			mMapView.getController().animateTo( point );
		}
	}
}