package com.android.shuomi.around;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.shuomi.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class AroundOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> mItems = new ArrayList<OverlayItem>(); 
    private Drawable mMarker;
    private Context mContext;
    private View mPopupView = null;
    private MapView mMapView = null;
    
	public AroundOverlay( Context context, MapView parent, View popupView, Drawable defaultMarker ) {
		super(defaultMarker);
		this.mContext = context;
		this.mMarker = defaultMarker;
		this.mPopupView = popupView;
		this.mMapView = parent;
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
	private void focusChanged( ItemizedOverlay overlay, OverlayItem newFocus )
	{
		if ( mPopupView != null ) 
		{
			mPopupView.setVisibility( View.GONE );
			 
			if ( newFocus != null ) 
			{
				MapView.LayoutParams layout = (MapView.LayoutParams) mPopupView.getLayoutParams();
				
//				layout.point = newFocus.getPoint();
				
				Point point = new Point();
				mMapView.getProjection().toPixels( newFocus.getPoint(), point );
				
//				int y = 0;
//				
//				if ( point.y > 35 )
//				{
//					y = point.y - 35;
//					layout.alignment = MapView.LayoutParams.BOTTOM_CENTER | MapView.LayoutParams.CENTER_HORIZONTAL;
//				}
//				else 
//				{
//					y = point.y + 3;
//					layout.alignment = MapView.LayoutParams.TOP | MapView.LayoutParams.CENTER_HORIZONTAL;
//				}
				
				int y = point.y - 35;
				layout.alignment = MapView.LayoutParams.BOTTOM_CENTER;
				
				mMapView.getProjection().toPixels( mMapView.getMapCenter(), point );
				int x = point.x;
				
				layout.point = mMapView.getProjection().fromPixels( x, y );
				
//				layout.mode = MapView.LayoutParams.MODE_VIEW;
//				layout.x = point.x;
//				layout.y = point.y - 32;				
				 
				TextView description = (TextView) mPopupView.findViewById( R.id.details );
				description.setText( "Title" );
				 
				TextView category = (TextView) mPopupView.findViewById( R.id.category );
				category.setText( "Category" );
				 
				mMapView.updateViewLayout( mPopupView, layout );
				mPopupView.setVisibility( View.VISIBLE );
			}
		 }
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
    public void draw( Canvas canvas, MapView mapView, boolean shadow)  {  
        super.draw( canvas, mapView, shadow );
        
        int index = this.getIndexToDraw(1);
        
        Log.d( "AroundOverlay", "index to draw = " + index  );
        //boundCenterBottom( mMarker );
        boundCenterBottom( this.getItem( index ).getMarker( 0 ) );
    }
	
	public void addOverlay(OverlayItem item) {
		mItems.add( item );
        populate();
    }
	
//	@Override
//	public boolean onTap( GeoPoint p, MapView mapView )
//	{
//		return false;
//	}
	

	protected boolean onTap( int index )
	{
		Log.d( "AroundOverlay", "onTap index = " + index );
		
//		if ( mPopupView != null ) 
//		{
//			mPopupView.setVisibility( View.GONE );
//			 
//			MapView.LayoutParams geoLP = (MapView.LayoutParams) mPopupView.getLayoutParams();
//			geoLP.point = mItems.get( index ).getPoint(); 
//			//mMapView.getOverlays().get(index); 
//				 
//			TextView description = (TextView) mPopupView.findViewById( R.id.details );
//			description.setText( "Title" );
//				 
//			TextView category = (TextView) mPopupView.findViewById( R.id.category );
//			category.setText( "Category" );
//			
//			mMapView.updateViewLayout( mPopupView, geoLP );
//			mPopupView.setVisibility( View.VISIBLE );
//		 }
		
		return true;
	}
}
