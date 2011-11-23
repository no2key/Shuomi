package com.android.shuomi.around;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.shuomi.NetworkRequestLayout;
import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.around.MapInterface.LocationUpdateListener;
import com.android.shuomi.baidumap.BaiduMap;
import com.android.shuomi.groupon.GrouponDetailsView;
import com.android.shuomi.groupon.GrouponMainView;
import com.android.shuomi.intent.GrouponAroundRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.parser.ResponseParser;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;

public class GrouponAroundMapView extends NetworkRequestLayout 
{
	private static final String TAG = "GrouponAroundMapView";
	private static final int DEFAULT_DISTANCE = 5000;
	private static final int POPUP_VIEW_ALPHA = 200;
	private static final int[] CATEGORY = GrouponMainView.GROUPON_LIST_ITEM;
	
	private final int[] mPinResIds = { R.drawable.ic_pin_red, R.drawable.ic_pin_blue, 
			R.drawable.ic_pin_yellow, R.drawable.ic_pin_green, R.drawable.ic_pin_purple, R.drawable.ic_pin_black };
	
	private final String[] mKeys = { "cate", "hotel", "shopping", "entertainment", "healthy", "others" };
	
	private MapInterface mMap = null;
	private ArrayList<String[]> mItemList = null;
	private HashMap< GeoPoint , Integer> mPointTable = null;
	private View mPopupView = null;
	private LocationUpdateListener mUpdateListener = null;
	
	public GrouponAroundMapView( Context context ) 
	{
		super(context);
		mMap = BaiduMap.getInstance( context );//new BaiduMap( context );
		
		Log.d( TAG, "view init" );
		inflateLayout();
		Log.d( TAG, "view inflated" );
		startUpdateLocation();		
		Log.d( TAG, "view done" );
	}
	
	private void inflateLayout() 
	{
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.groupon_around_baidu, this, true );
		
		setupTitle();
		createPopupView( layoutInflater );
		
		mMap.start();
		mMap.setupMapView( this, R.id.mapview, mPopupView, new SetupPopupViewInterface() 
		{
			@Override
			public void setup( View popupView, MapView.LayoutParams popupViewLayout, OverlayItem item ) 
			{
				setupPopupView( popupView, popupViewLayout, item );				
			}
		});
	}
	
	private void setupPopupView( View popupView, MapView.LayoutParams popupViewLayout, final OverlayItem item )
	{
		if ( popupView != null && item != null )
		{
			LinearLayout textBoxParent = (LinearLayout) popupView.findViewById( R.id.text_box_parent );
			LinearLayout textBox = (LinearLayout) popupView.findViewById( R.id.text_box );
			
			int imageWidth = 0;
			
			ImageView indicator = (ImageView) popupView.findViewById( R.id.indicator );
			
			if ( indicator != null )
			{
				LayoutParams layoutParam = (LayoutParams) indicator.getLayoutParams();
				imageWidth = indicator.getDrawable().getIntrinsicWidth() + 
						     indicator.getPaddingLeft() + indicator.getPaddingRight() + 				
						     layoutParam.leftMargin + layoutParam.rightMargin;
			}
			
			Log.d( TAG, "popup view, view width: " + popupViewLayout.width + ", image width: " + imageWidth );
			
			LayoutParams layout = new LayoutParams( popupViewLayout.width - imageWidth, ViewGroup.LayoutParams.WRAP_CONTENT );
			textBoxParent.updateViewLayout( textBox, layout );
			
			TextView description = (TextView) popupView.findViewById( R.id.details );
			
			if ( description != null )
			{
				description.setText( item.getTitle() );
			}
			 
			TextView category = (TextView) popupView.findViewById( R.id.category );
			
			if ( category != null )
			{
				category.setText( item.getSnippet() );
			}
			
			indicator.setOnClickListener( new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					goToDetailsView( item.getPoint() );
				}
			} );	
		}
	}
	
	private void goToDetailsView( GeoPoint point )
	{
		if ( mPointTable != null )
		{
			Integer index = mPointTable.get( point );
			
			if ( index.intValue() < mItemList.size() )
			{
				String id = mItemList.get( index.intValue() )[8];
				( ( ServiceListView ) getContext() ).goToNextView
					( new GrouponDetailsView( getContext(), id, RESPONSE.HTTP_RESPONSE_AROUND ) );
			}
		}
	}
	
	private void setupTitle() 
	{
		TextView title = (TextView) findViewById( R.id.titlebar_label );
		title.setText( R.string.groupon_around );
		
		Button refresh = (Button) findViewById( R.id.titlebar_button );
		refresh.setText( R.string.refresh_location );
		refresh.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refreshLocation();
			}
		});
	}
	
	private void refreshLocation()
	{
		mMap.clearMyLocationOverlay();
		mMap.clearOverlays();
		startUpdateLocation();
		
//		if ( mUpdateListener != null )
//		{
//			mMap.registerLocationUpdate( mUpdateListener );
//		}
	}
	
	private void createPopupView( LayoutInflater inflater )
	{
		mPopupView = inflater.inflate( R.layout.map_popup, null );
	    mPopupView.getBackground().setAlpha( POPUP_VIEW_ALPHA );
	}
	
	private void startUpdateLocation()
	{
		mUpdateListener = new LocationUpdateListener() 
		{
			@Override
			public void locationUpated( GeoPoint geoPoint ) 
			{
				mMap.setZoom( 14 );
				mMap.moveToPoint( geoPoint );
				requestGrouponAround( geoPoint );
				initOverlays();
			}
		};
		
		mMap.registerLocationUpdate( mUpdateListener );
	}
	
	private void requestGrouponAround( GeoPoint point ) 
	{
		if ( point != null && point.getLatitudeE6() != 0 && point.getLongitudeE6() != 0 )
		{
			GrouponAroundRequestIntent request = new GrouponAroundRequestIntent( 
					getClass().getName() , point.getLatitudeE6(), point.getLongitudeE6(), DEFAULT_DISTANCE );
			sendRequest( request );
		}
	}
	
	private void initOverlays()
	{
		mMap.clearOverlays();
		
		for ( int i = 0; i < CATEGORY.length; i ++ )
		{
			mMap.createOverlay( mKeys[i], mPinResIds[i] );
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void update( String requestAction, int dataType, Object data, int pageCount ) 
	{
		if ( requestAction.equals( REQUEST.AROUND_GROUPON ) && data != null )
		{
			if ( dataType == ResponseParser.TYPE_ARRAY_LIST ) 
			{
				Log.d( TAG,  "update view start" );
				mMap.unregisterLocationUpdate();
				mItemList = (ArrayList<String[]>) data;
				mPointTable = new HashMap<GeoPoint, Integer>();
				updateView();
				Log.d( TAG,  "update view end" );
			}
		}
	}
	
	private void updateView() 
	{
		if ( mItemList != null && mItemList.size() > 0 )
		{
			for ( int i = 0; i < mItemList.size(); i ++ )
			{
				GeoPoint point = addMarker( mItemList.get(i) );
				
				if ( point != null )
				{
					mPointTable.put( point, Integer.valueOf( i ) );
				}
			}
			
			mMap.commitOverlays();
			mMap.setZoom( 13 );
		}
	}
	
	private GeoPoint addMarker( String[] item ) 
	{
		GeoPoint point = null;
		
		int category = findCategory( item[10] );
		
//		if ( category >= CATEGORY.length )
//		{
//			category = CATEGORY.length - 1;
//		}
		
		if ( category > -1 && category < CATEGORY.length )
		{
			int longtitudeE6 = (int) (Float.parseFloat( item[13] ) * 1E6);
			int latitudeE6 = (int) (Float.parseFloat( item[14] ) * 1E6);
			point = new GeoPoint( latitudeE6, longtitudeE6 );
			Log.d( TAG, point.toString() );
			
			OverlayItem overlayItem = new OverlayItem( point, item[5], item[10] );
			mMap.addOverlayItem( mKeys[category] , overlayItem );
		}
		
		return point;
	}
	
	private int findCategory( String type )
	{
		int category = -1;
		
		for ( int i = 0; i < CATEGORY.length; i ++ )
		{
			if ( type.equals( getContext().getString( CATEGORY[i] ) ) )
			{
				category = i;
				break;
			}
		}
		
		return category;
	}
}