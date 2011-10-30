package com.android.shuomi.around;

import java.util.ArrayList;

import com.android.shuomi.NetworkRequestLayout;
import com.android.shuomi.R;
import com.android.shuomi.groupon.GrouponMainView;
import com.android.shuomi.intent.GrouponAroundRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.parser.ResponseParser;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import android.R.integer;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GrouponAroundView extends NetworkRequestLayout {

	private static final String TAG = "GrouponAroundView";
	private static final int DEFAULT_DISTANCE = 5000;
	private static final int[] CATEGORY = GrouponMainView.GROUPON_LIST_ITEM;
	private MapView mMapView;
	private MyLocationOverlay mMyLocationOverlay = null;
	private GeoPoint mMyGeoPoint; 
	private ArrayList<String[]> mItemList = null;
	private View mPopupView = null;
	private AroundOverlay[] mOverlays= new AroundOverlay[CATEGORY.length];
	private int[] mPinResIds = { R.drawable.ic_pin_red, R.drawable.ic_pin_blue, 
			R.drawable.ic_pin_yellow, R.drawable.ic_pin_black, R.drawable.ic_pin_black, R.drawable.ic_pin_black };
	
	public GrouponAroundView( Context context ) {
		super(context);
		Log.d( TAG, "GrouponAroundView init" );
		inflateLayout();
		goToMyLocation();
		Log.d( TAG, "GrouponAroundView done" );
	}
	
	private void initOverlays()
	{
		for ( int i = 0; i < CATEGORY.length; i ++ )
		{
			mOverlays[i] = new AroundOverlay( getContext(), mMapView, mPopupView, 
					getResources().getDrawable( mPinResIds[i] ) );
		}
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.groupon_around, this, true );
		
		setupTitle();
		initView();
		createPopupView( layoutInflater );
	}
	
	private void createPopupView( LayoutInflater inflater )
	{
		mPopupView = inflater.inflate( R.layout.map_popup, null );
		Log.d( TAG, "width = " + mMapView.getWidth()  );
	    mMapView.addView( mPopupView, 
	             new MapView.LayoutParams( /*MapView.LayoutParams.FILL_PARENT*/200, MapView.LayoutParams.WRAP_CONTENT,
	            		 null, MapView.LayoutParams.BOTTOM_CENTER | MapView.LayoutParams.CENTER_HORIZONTAL ) );
	    mPopupView.getBackground().setAlpha( 170 );
	    mPopupView.setVisibility( View.GONE );
	}
	
	public View getPopupView()
	{
		return mPopupView;
	}
	
	private void initView()
	{
		mMapView = (MapView) findViewById( R.id.mapview );
		mMapView.setTraffic( false );
		mMapView.setBuiltInZoomControls( true );
		mMapView.displayZoomControls( true );
		mMapView.getController().setZoom( 13 );
	}
	
	private void setupTitle() 
	{
		TextView title = (TextView) findViewById( R.id.titlebar_label );
		title.setText( R.string.groupon_around );
		
		Button refresh = (Button) findViewById( R.id.titlebar_button );
		refresh.setText( R.string.refresh );
	}
	
	private void clearMyLocationOverlay()
	{
		if ( mMyLocationOverlay != null )
		{
			mMapView.getOverlays().remove( mMyLocationOverlay );
		}
	}
	
	private void goToMyLocation()
	{
		clearMyLocationOverlay();
		
		mMyLocationOverlay = new MyLocationOverlay( getContext(), mMapView );
		mMyLocationOverlay.enableMyLocation();
		
		mMyLocationOverlay.runOnFirstFix( new Runnable() 
		{
			public void run() 
			{
				mMyGeoPoint = mMyLocationOverlay.getMyLocation();
				Log.d( TAG, "on first fix, " + mMyGeoPoint.toString());
				mMapView.getController().animateTo( mMyGeoPoint );
				requestGrouponAround( mMyGeoPoint );
            }
        } );

		mMapView.getOverlays().add( mMyLocationOverlay );
	}
	
	private void requestGrouponAround( GeoPoint point ) 
	{
		if ( point != null && point.getLatitudeE6() != 0 && point.getLongitudeE6() != 0 )
		{
			GrouponAroundRequestIntent request = new GrouponAroundRequestIntent( 
					getClass().getName() , point.getLatitudeE6(), point.getLongitudeE6(), DEFAULT_DISTANCE );
			sendRequest( request );
			initOverlays();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update( String requestAction, int dataType, Object data, int pageCount ) {
		Log.d( TAG,  "update" );
		if ( requestAction.equals( REQUEST.AROUND_GROUPON ) && data != null )
		{
			Log.d( TAG,  "data validated" );
			if ( dataType == ResponseParser.TYPE_ARRAY_LIST ) 
			{
				Log.d( TAG,  "update view" );
				mItemList = (ArrayList<String[]>) data;
				updateView();
			}
		}
	}

	private void updateView() 
	{
		if ( mItemList != null && mItemList.size() > 0 )
		{
			for ( int i = 0; i < mItemList.size(); i ++ )
			{
				addMarker( mItemList.get(i) );
			}
			
			commitUpdate();
		}
	}
	
	private int findCategory( String type ) 
	{
		int category;

		for ( category = 0; !type.equals( getContext().getString( CATEGORY[category] ) ); category ++  );
		
		if ( category >= CATEGORY.length )
		{
			category = CATEGORY.length - 1;
		}
		
		return category;
	}
	
	private void addMarker( String[] item ) 
	{
		int category = findCategory( item[10] );
		
		int longtitudeE6 = (int) (Float.parseFloat( item[13] ) * 1E6);
		int latitudeE6 = (int) (Float.parseFloat( item[14] ) * 1E6);
		GeoPoint point = new GeoPoint( latitudeE6, longtitudeE6 );
		Log.d( TAG, point.toString() );
		
		OverlayItem overlayItem = new OverlayItem( point, item[5], item[10] );
		//overlayItem.setMarker( getResources().getDrawable( R.drawable.ic_pin_blue ) );

		//Drawable drawable = getResources().getDrawable( R.drawable.ic_pin_yellow );		
		//AroundOverlay aroundOverlay = new AroundOverlay( getContext(), mMapView, mPopupView, drawable );
		//aroundOverlay.addOverlay( overlayItem );
		mOverlays[category].addOverlay( overlayItem );
		
		//mMapView.getOverlays().add( aroundOverlay );
	}
	
	private void commitUpdate()
	{
		for ( int i = 0; i < mOverlays.length; i ++ ) {
			//mOverlays[i].populate();
			mMapView.getOverlays().add( mOverlays[i] );
		}
	}
}