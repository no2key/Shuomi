package com.android.shuomi.baidumap;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.android.shuomi.around.SetupPopupViewInterface;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;

public class BaiduOverlay extends ItemizedOverlay<OverlayItem> 
{
	static private final String TAG = "BaiduOverlay";
	
	private List<OverlayItem> mItems = new ArrayList<OverlayItem>();
	private Drawable mMarker;
	//private Context mContext;
	private View mPopupView;
	private MapView mMapView;
	private SetupPopupViewInterface mSetupPopListener;

	public BaiduOverlay( Drawable marker, Context context, MapView mapView, View popupView, SetupPopupViewInterface listener ) 
	{
		super( marker );
		
		this.mMarker = marker;
		//this.mContext = context;
		this.mPopupView = popupView;
		this.mMapView = mapView;
		this.mSetupPopListener = listener;
	}

	@Override
	protected OverlayItem createItem( int i ) 
	{
		return mItems.get( i );
	}

	@Override
	public int size() 
	{
		return mItems.size();
	}
	
	public void addItem( OverlayItem item ) 
	{
		if ( item != null )
		{
			mItems.add( item );
			populate();
		}
    }
	
	@Override
	public void draw( Canvas canvas, MapView mapView, boolean shadow )
	{
		super.draw( canvas, mapView, shadow );
		boundCenterBottom( mMarker );
	}
	
	@Override
	protected boolean onTap( int i ) 
	{
		OverlayItem item = mItems.get( i );
		setFocus( item );
		showPopupView( item );		
		return true;
	}

	@Override
	public boolean onTap(GeoPoint point, MapView view ) 
	{
		mPopupView.setVisibility( View.GONE );
		return super.onTap( point, view );
	}

	private void showPopupView( final OverlayItem item )
	{
		if ( mPopupView != null && item != null ) 
		{
			mPopupView.setVisibility( View.VISIBLE );
			
			MapView.LayoutParams layout = calculatePopupLayout( item );
			mMapView.updateViewLayout( mPopupView, layout );
			
			if ( mSetupPopListener != null )
			{
				mSetupPopListener.setup( mPopupView, layout, item );
			}
		}
	}
	
	private MapView.LayoutParams calculatePopupLayout( OverlayItem item )
	{
		Point point = new Point();
		
		mMapView.getProjection().toPixels( item.getPoint(), point );
		int y = point.y - 50;
		
		mMapView.getProjection().toPixels( mMapView.getMapCenter(), point );
		int x = point.x;
		
		Log.d( TAG, "screen width = " + x * 2 );
		
		MapView.LayoutParams layout = new MapView.LayoutParams( 
				x * 2 - 20,
//				MapView.LayoutParams.FILL_PARENT,
				MapView.LayoutParams.WRAP_CONTENT,
				mMapView.getProjection().fromPixels( x, y ), MapView.LayoutParams.BOTTOM_CENTER );
		
		return layout;
	}
}
