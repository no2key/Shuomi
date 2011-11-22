package com.android.shuomi;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.OverlayItem;

public interface MapInterface 
{
	public void create();
	public void start();
	public void stop();
	public void destroy();
	
	public void setupMapView( View parent, int mapViewResId );
	public void moveToPoint( GeoPoint pt );
	
	public interface LocationUpdateListener
	{
		public void locationUpated( GeoPoint geoPoint );
	}
	
	public void registerLocationUpdate( LocationUpdateListener listener );
	public void unregisterLocationUpdate();
	
	public ItemizedOverlay<OverlayItem> createOverlay( String name, int markerResId );
	public ItemizedOverlay<OverlayItem> createOverlay( String name, Drawable marker );

	public void addOverlayItem( String name, OverlayItem item );
	public void commitOverlays();
	
	public void clearOverlays();
	public void clearMyLocationOverlay();
	
	public void setZoom( int zoom );
	
	//public void addOverlayItem( ItemizedOverlay<OverlayItem> overlay, OverlayItem item );
	//public void addOverlayItem( GeoPoint point, String title, String snippet );

}
