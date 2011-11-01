package com.android.shuomi.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;

import com.android.shuomi.BackableView;
import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.SimpleListLayout;
import com.android.shuomi.UI;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.util.Util;

public class WeatherProvinceListView extends SimpleListLayout implements BackableView {

private static final String TAG = "WeatherProvinceListView";
	
	private final int PROVINCE_LIST = 0;
	private final int CITY_LIST = 1;
	private final int COUNTY_LIST = 2;
	private final int SELECTED = 3;	
	private final String mReqAction[] = { REQUEST.WEATHER_PROVINCE, REQUEST.WEATHER_CITY, REQUEST.WEATHER_COUNTY };
	private final int[] mTitleIds = { R.string.province_list, R.string.city_list, R.string.county_list };
	private int mState = PROVINCE_LIST;
	private Stack <SimpleAdapter> mAdapterStack = new Stack<SimpleAdapter>();
	private Stack<ArrayList<String[]>> mArrayListStack = new Stack<ArrayList<String[]>>();
	
	public WeatherProvinceListView(Context context) {
		super(context);
		getList().getBackground().setAlpha( UI.VIEW_DEFAULT_ALPHA );		
		enableButton( false );
		onStateChange( 0 );
	}
	
	@Override
	protected void onItemSingleClick( View view, int position, long rowId ) {
		onSelected( position );
	}
	
	private void onSelected( int position ) {
		mState ++;		
		onStateChange( position );
	}
	
	private boolean onStateChange( int position ) {
		boolean canBackTo = false;
		
		switch ( mState ) {
		
		case PROVINCE_LIST:
		case CITY_LIST:
		case COUNTY_LIST:
			
			setTitleText( mTitleIds[mState] );
			showLocationList( position );
			canBackTo = true;
			break;
			
		case SELECTED:
			requestFinish( position );
			break;
		
		default:
			Log.w( TAG, "nothing to do" );
		}
		
		return canBackTo;
	}
	
	private void showLocationList( int position ) {
		if ( !showCachedList() ) 
		{
			requestList( getLocationId( position ) );
		}
	}
	
	private String getLocationId( int position ) {
		String id = null;
		ArrayList<String[]> dataList = mArrayListStack.empty() ? null : mArrayListStack.peek();
		
		if ( dataList != null && dataList.size() > position )
		{
			id = dataList.get( position )[0];
		}
		
		return id;
	}
	
	private boolean showCachedList() {
		boolean ok = false;		
	
		if ( mState == mAdapterStack.size() -1  ) 
		{
			SimpleAdapter adapter = mAdapterStack.pop();
			
			if ( adapter != null ) 
			{
				setSimpleListAdapter( adapter );
				ok = true;
			}
			
			mArrayListStack.pop();
		}
		
		return ok;
	}
	
	private void requestList( String param ) {
		RequestIntent request = new RequestIntent( mReqAction[mState] );
		request.setResponseAction( RESPONSE.HTTP_RESPONSE_WEATHER );
		request.setSourceClass( getClass().getName() );
		
		if ( Util.isValid( param ) ) {
			request.setParam1( param );
		}
		
		sendRequest( request );
	}
	
	private void requestFinish( int position ) {		
		( ( ServiceListView ) getContext() ).onWeatherLocationSelected( getLocationId( position ) );
	}

	@Override
	public boolean back() {
		mState --;
		return onStateChange( 0 );
	}

	@Override
	protected String getRequestAction() {
		return mReqAction[mState];
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.simple_list_with_titlebar;
	}
	
	@Override
	protected void updateArrayList( int page, ArrayList<String[]> itemList ) {
		enqueueCurrent( itemList );
		
		SimpleAdapter adapter = new SimpleAdapter ( getContext(), getAdapterArrayList( itemList ), 
				mItemResId, mMapKeys, mMapValues );
		setSimpleListAdapter( adapter );
	}
	
	private void enqueueCurrent( ArrayList<String[]> itemList ) {
		SimpleAdapter currentAdapter = (SimpleAdapter) getList().getAdapter();
		
		if ( currentAdapter != null ) 
		{
			mAdapterStack.push( currentAdapter );
		}
		
		if ( itemList != null ) {
			mArrayListStack.push( itemList );
		}
	}
	
	private ArrayList< HashMap<String, Object> > getAdapterArrayList( ArrayList<String[]> itemList ) {
        ArrayList< HashMap<String, Object> > arrayList = null;
        
        if ( itemList != null ) 
        {
        	arrayList = new ArrayList< HashMap<String,Object> >();
        	
	        for ( int i = 0; i < itemList.size(); i ++ ) 
	        {  
	            HashMap <String, Object> map = new HashMap< String, Object >();  
	            map.put( LABEL_KEY, itemList.get(i)[1] );
	            arrayList.add( map );
	        }
        }
          
        return arrayList;
    }
}
