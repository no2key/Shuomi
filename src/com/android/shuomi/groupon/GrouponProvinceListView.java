package com.android.shuomi.groupon;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.android.shuomi.BackableView;
import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.SimpleListLayout;
import com.android.shuomi.UI;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;

public class GrouponProvinceListView extends SimpleListLayout implements BackableView {
	
	private static final String TAG = "GrouponProvinceListView";
	
	private final int PROVINCE_LIST = 0;
	private final int CITY_LIST = 1;
	private final int CITY_SELECTED = 2;
	
	private final String mReqAction[] = { REQUEST.LIST_PROVINCE, REQUEST.LIST_CITY };
	private int mState = PROVINCE_LIST;
	private BlockingQueue <SimpleAdapter> m_AdapterQueue = new LinkedBlockingQueue<SimpleAdapter>();
	private String mProvince = null;

	public GrouponProvinceListView( Context context ) {
		super(context);
		enableButton( false );
		setTitleText( R.string.province_list );
		getList().getBackground().setAlpha( UI.VIEW_DEFAULT_ALPHA );
		onStateChange( 0 );
	}

	@Override
	protected String getRequestAction() {
		return mReqAction[mState];
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
			showProvinceList();
			canBackTo = true;
			break;
			
		case CITY_LIST:			
			mProvince = getListLabel( position );
			
			if ( position == 0 ) {
				requestFinish( mProvince, mProvince );
			}
			else {
				requestList( mProvince );
			}
			
			break;
			
		case CITY_SELECTED:
			requestFinish( mProvince, getListLabel( position ) );
			break;
		
		default:
			Log.w( TAG, "nothing to do" );
		}
		
		return canBackTo;
	}
	
	private void showProvinceList() {
		if ( !showCachedList() ) {
			requestList( null );
		}
	}
	
	private boolean showCachedList() {
		boolean ok = false;		
		SimpleAdapter adapter = m_AdapterQueue.poll();
		
		if ( adapter != null ) {
			setSimpleListAdapter( adapter );
			ok = true;
		}
		
		return ok;
	}
	
	private void requestList( String param ) {
		RequestIntent request = new RequestIntent( mReqAction[mState] );
		request.setResponseAction( RESPONSE.HTTP_RESPONSE_GROUPON );
		request.setSourceClass( getClass().getName() );
		
		if ( Util.isValid( param ) ) {
			request.setParam1( param );
		}
		
		sendRequest( request );
	}
	
	private String getListLabel( int position ) {
		SimpleAdapter adapter = (SimpleAdapter) getList().getAdapter();
		@SuppressWarnings("unchecked")
		HashMap <String, String> map = (HashMap<String, String>) adapter.getItem( position );
		return (String) map.get( LABEL_KEY );
	}

	private void requestFinish( String province, String city ) {
		( ( ServiceListView ) getContext() ).onCitySelected( province, city );
	}
	
	@Override
	public boolean back() {
		mState --;
		return onStateChange( 0 );
	}
	
	@Override
	protected void updateSimpleList( String[] items ) {
		SimpleAdapter adapter = (SimpleAdapter) getList().getAdapter();
		
		if ( adapter != null ) {
			m_AdapterQueue.add( adapter );
		}
		
		super.updateSimpleList( items );
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.simple_list_with_titlebar;
	}
}
