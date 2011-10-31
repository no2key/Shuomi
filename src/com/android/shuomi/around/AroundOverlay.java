package com.android.shuomi.around;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.shuomi.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class AroundOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> mItems = new ArrayList<OverlayItem>(); 
    private Drawable mMarker;
    private View mParent;
    private View mPopupView = null;
    private MapView mMapView = null;
    
	public AroundOverlay( View parent, MapView mapView, View popupView, Drawable defaultMarker ) {
		super(defaultMarker);
		this.mParent = parent;
		this.mMarker = defaultMarker;
		this.mPopupView = popupView;
		this.mMapView = mapView;
		addFocusChangeListener();
	}
	
	private void addFocusChangeListener()
	{
		setOnFocusChangeListener( new OnFocusChangeListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onFocusChanged( ItemizedOverlay overlay, OverlayItem newFocus ) 
			{
				Log.d( "AroundOverlay", "onFocusChanged" );
				focusChanged( overlay, newFocus );
			}
		});

	}
	
	@SuppressWarnings("rawtypes")
	private void focusChanged( ItemizedOverlay overlay, final OverlayItem newFocus )
	{
		if ( mPopupView != null ) 
		{
			mPopupView.setVisibility( View.GONE );
			 
			if ( newFocus != null ) 
			{
				TextView description = (TextView) mPopupView.findViewById( R.id.details );
				description.setText( newFocus.getTitle() );
				 
				TextView category = (TextView) mPopupView.findViewById( R.id.category );
				category.setText( newFocus.getSnippet() );
				
				mPopupView.findViewById( R.id.indicator ).setOnClickListener( new OnClickListener() 
				{
					@Override
					public void onClick(View v)
					{
						goToDetailsView( newFocus.getPoint() );
					}
				} );

				MapView.LayoutParams layout = calculatePopupLayout( newFocus );
				mMapView.updateViewLayout( mPopupView, layout );
				mPopupView.setVisibility( View.VISIBLE );
			}
		}
	}
	
	private void goToDetailsView( GeoPoint point )
	{
		( ( GrouponAroundView ) mParent ).goToDetailsView( point );
	}
	
	private MapView.LayoutParams calculatePopupLayout( OverlayItem newFocus )
	{
		Point point = new Point();
		
		mMapView.getProjection().toPixels( newFocus.getPoint(), point );
		int y = point.y - 35;
		
		mMapView.getProjection().toPixels( mMapView.getMapCenter(), point );
		int x = point.x;
		
		MapView.LayoutParams layout = new MapView.LayoutParams( x * 2 - 20, MapView.LayoutParams.WRAP_CONTENT,
				mMapView.getProjection().fromPixels( x, y ), MapView.LayoutParams.BOTTOM_CENTER );
		
		return layout;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mItems.get(i);
	}

	@Override
	public int size() {
		return mItems.size();
	}

	@Override  
    public void draw( Canvas canvas, MapView mapView, boolean shadow )  {  
        super.draw( canvas, mapView, shadow );

        boundCenterBottom( mMarker );
        
//        int index = this.getIndexToDraw(1);
//        
//        Log.d( "AroundOverlay", "index to draw = " + index  );
//        
//        boundCenterBottom( this.getItem( index ).getMarker( 0 ) );
    }
	
	public void addOverlay(OverlayItem item) {
		mItems.add( item );
        populate();
    }
	
	protected boolean onTap( int index )
	{
		Log.d( "AroundOverlay", "onTap index = " + index );
		return true;
	}
}
