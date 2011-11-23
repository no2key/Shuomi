package com.android.shuomi.baidumap;

import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.around.MapInterface;
import com.android.shuomi.around.SetupPopupViewInterface;
import com.android.shuomi.util.EventIndicator;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.OverlayItem;

public class BaiduMap implements MapInterface 
{
	private class StatusListener implements MKGeneralListener 
	{
		@Override
		public void onGetNetworkState( int iError ) 
		{
			EventIndicator.showToast( mContext, R.string.network_error );
		}

		@Override
		public void onGetPermissionState( int iError ) 
		{
			if ( iError ==  MKEvent.ERROR_PERMISSION_DENIED ) 
			{
				EventIndicator.showToast( mContext, R.string.invalid_bmap_key );
				mKeyValid = false;
			}
		}
	}
	
	static final public String TAG = "BaiduMap";
	static final public String MAP_KEY = "76473C77A5838461D53EF68DB430819F7C4711C4";
	
	private BMapManager mBMapMan = null;	
	private Context mContext = null;
	@SuppressWarnings("unused")
	private boolean mKeyValid = true;
	
	private MapView mMapView = null;
	private LocationListener mLocationListener = null;
	private MyLocationOverlay mMyLocationOverlay = null;
	private HashMap<String, BaiduOverlay> mOverlays = new HashMap<String, BaiduOverlay>();
	private View mPopupView = null;
	private SetupPopupViewInterface mSetupPopListener;
	private boolean mRequestLoaction = true;
	
	static private BaiduMap mInstance = null;
	
	static public final BaiduMap getInstance( Context context )
	{
		if ( mInstance == null )
		{
			mInstance = new BaiduMap( context );
		}
		
		return mInstance;
	}
	
	public BaiduMap( Context context )
	{
		mContext = context;
		mRequestLoaction = true;
	}
	
	@Override
	public void create()
	{
		mBMapMan = new BMapManager( mContext );
		mBMapMan.init( MAP_KEY, new StatusListener() );
	}

	@Override
	public void start() 
	{
		mBMapMan.start();
		ServiceListView.getInstance().initMapActivity( mBMapMan );
	}

	@Override
	public void stop() 
	{
		mBMapMan.stop();
	}

	@Override
	public void destroy() 
	{
		mBMapMan.destroy();
		mBMapMan = null;
	}

	@Override
	public void setupMapView( View parent, int mapViewResId, View popupView, SetupPopupViewInterface listener ) 
	{
		if ( parent != null && popupView != null )
		{
			mMapView = ( MapView ) parent.findViewById( R.id.mapview );
			mMapView.setTraffic( false );
			mMapView.setBuiltInZoomControls( true );
			mMapView.displayZoomControls( true );
			mMapView.getController().setZoom( 13 );		
	        mMapView.setDrawOverlayWhenZooming( true );
	        
	        mPopupView = popupView;
	        mSetupPopListener = listener;
	        
	        mMapView.addView( mPopupView, 
		             new MapView.LayoutParams( 200, MapView.LayoutParams.WRAP_CONTENT,
		             null, MapView.LayoutParams.BOTTOM_CENTER ) );
	        mPopupView.setVisibility( View.GONE );
	        
//	        Log.d( TAG, "createMyLoactionOverLay" );
//			mMyLocationOverlay = new MyLocationOverlay( mContext, mMapView ) ;
//			mMyLocationOverlay.enableMyLocation();
//			mMapView.getOverlays().add( mMyLocationOverlay );
		}
	}
	
	private void createMyLoactionOverLay( final LocationUpdateListener listener )
	{
		//if ( mMyLocationOverlay == null )
		{
			Log.d( TAG, "createMyLoactionOverLay" );
			mMyLocationOverlay = new MyLocationOverlay( mContext, mMapView ) ;
			mMapView.getOverlays().add( mMyLocationOverlay );
			
	        mLocationListener = new LocationListener()
	        {
				@Override
				public void onLocationChanged( Location location ) 
				{
					if ( location != null )
					{
						GeoPoint pt = new GeoPoint( ( int ) ( location.getLatitude() * 1e6 ), ( int )( location.getLongitude() * 1e6 ) );
						
						if ( listener != null )
						{
							Log.d( TAG,  pt.toString() );
							
							if ( mRequestLoaction )
							{
								mRequestLoaction = false;
								listener.locationUpated( pt );
							}
						}
					}
				}
	        };
		}
	}
	
	@Override
	public void moveToPoint( GeoPoint pt )
	{
		mMapView.getController().animateTo( pt );
	}

	@Override
	public void registerLocationUpdate( LocationUpdateListener listener )
	{
		mBMapMan.stop();
		createMyLoactionOverLay( listener );
		mBMapMan.getLocationManager().requestLocationUpdates( mLocationListener );
		mBMapMan.start();
		mMyLocationOverlay.enableMyLocation();
		Log.d( TAG, "registerLocationUpdate" );
		
	}

	@Override
	public void unregisterLocationUpdate() 
	{
		Log.d( TAG, "unregisterLocationUpdate" );
		//mBMapMan.getLocationManager().removeUpdates( mLocationListener );
		//mBMapMan.stop();
	}

	@Override
	public void addOverlayItem( String name, OverlayItem item ) 
	{
		if ( !TextUtils.isEmpty( name ) && item != null )
		{
			BaiduOverlay overlay = mOverlays.get( name );
			
			if ( overlay != null )
			{
				overlay.addItem( item );
				//Log.d( TAG, "add overlay item, key = " + name );
			}
//			else 
//			{
//				Log.e( TAG, "cannot find overlay, key = " + name );
//			}
		}
	}

	@Override
	public ItemizedOverlay<OverlayItem> createOverlay( String name, int markerResId ) 
	{
		BaiduOverlay overlay = null;
		
		if ( !TextUtils.isEmpty( name ) && markerResId > 0 )
		{
			Drawable marker = mContext.getResources().getDrawable( markerResId );
			marker.setBounds( 0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight() ); 
			
			overlay = (BaiduOverlay) createOverlay( name, marker );
		}
		
		return overlay;
	}

	@Override
	public ItemizedOverlay<OverlayItem> createOverlay( String name, Drawable marker ) 
	{
		BaiduOverlay overlay = null;
		
		if ( !TextUtils.isEmpty( name ) && marker != null )
		{
			overlay = new BaiduOverlay( marker, mContext, mMapView, mPopupView, mSetupPopListener );
			mOverlays.put( name, overlay );
			Log.d( TAG, "create overlay, key = " + name );
		}
		
		return overlay;
	}

	@Override
	public void commitOverlays() 
	{
		Set<String> keySet = mOverlays.keySet();
		String[] keys = new String[ keySet.size() ];
		keys = keySet.toArray( keys );
		
		for ( String key : keys )
		{
			BaiduOverlay overlay = mOverlays.get( key );
			Log.d( TAG, "key = " + key + ", item count = " + overlay.size() );
			
			if ( overlay.size() > 0 )
			{
				mMapView.getOverlays().add( overlay );
			}
		}
	}

	@Override
	public void clearOverlays() 
	{
		mMapView.getOverlays().removeAll( mOverlays.values() );
		mOverlays.clear();
		mPopupView.setVisibility( View.GONE );
	}

	@Override
	public void clearMyLocationOverlay() 
	{
		if ( mMyLocationOverlay != null )
		{
			mMapView.getOverlays().remove( mMyLocationOverlay );
		}
	}

	@Override
	public void setZoom( int zoom ) 
	{
		mMapView.getController().setZoom( zoom );
	}
}