package com.android.shuomi.groupon;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.intent.GrouponListRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.parser.ResponseParser;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;


public abstract class MultiItemListLayout extends PullToRefreshListLayout {

	private final String TAG = " MultiItemLayout ";
	private final int[] mItemFieldResIds = { R.id.demo_image, R.id.provider, R.id.discount, R.id.price, R.id.details };
	private final int mItemResId = R.layout.groupon_list_item;
	
	private boolean mLoadingInProgress = false;
	private int mDesiredPage = 1;
	private int mTotalPage = 1;
	private ArrayList<ListItemUnion[]> mDataList = null;
	private BaseAdapter mAdapter = null;
	
	public MultiItemListLayout(Context context) {
		super( context );
	}
	
	private void switchView() {
		findViewById( R.id.empty_view_loading ).setVisibility( View.GONE );
		getList().setVisibility( View.VISIBLE );
	}
	
	@Override
	protected void onItemSingleClick( View view, int position, long rowId ) {
		if ( mAdapter != null ) {
			ListItemUnion[] item = (ListItemUnion[]) mAdapter.getItem( position );
			String id = item[item.length-1].getString();
			Log.d( TAG, "click, id = " + id );
			( ( ServiceListView ) getContext() ).goToNextView( new GrouponDetailsView( getContext(),  id ) );
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void update( String requestAction, int dataType, Object data, int pageCount ) {
		
		if ( requestAction.equals( getRequestAction() ) ) {
			switchView();
			
			switch ( dataType ) {
			
			case ResponseParser.TYPE_ARRAY_LIST:
				mLoadingInProgress = false;
				updatePage( pageCount );
				updateMultiPageList( pageCount, ( ArrayList<String[]> ) data );
				setFooterOnResponse();
				break;
				
			default:
				break;
			}
		}
	}
	
	private void setFooterOnResponse() {
		if ( mDesiredPage == mTotalPage  ) {
			getList().removeFooterView( getFooterView() );
		}
		else {
			enableFooterView( false );
		}
	}
	
	protected void updatePage( int totalPage ) {
		mTotalPage = totalPage;
		mDesiredPage ++;
	}
	
	protected void updateMultiPageList( int page, ArrayList<String[]> itemList ) {
		if ( itemList != null ) {
			if ( mDataList == null ) {
				mDataList = new ArrayList<ListItemUnion[]>();
			}
			
			appendToDataList( itemList );
			
			if ( mAdapter == null ) {
				mAdapter = new MiscListAdapter( getContext(), mItemResId, mDataList, 
			    		null, null, mItemFieldResIds );
				getList().setAdapter( mAdapter );
				
				// TODO: to be checked
				//getList().setSelection( getList().getHeaderViewsCount() );
				setSelectionOnTop();
			}
			
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private void appendToDataList( ArrayList<String[]> fieldsList ) {
		if ( mDataList != null && fieldsList != null ) {
			for ( int i = 0; i < fieldsList.size(); i ++ ) {
				String[] fields = fieldsList.get( i );
				fields = convertFields( fields );
				ListItemUnion[] items = getListItemUnion( fields );
				mDataList.add( items );
			}
		}
	}
	
	private String[] convertFields( String[] fields ) {
//		float discount = 0;
//		
//		if ( Float.parseFloat( fields[2] ) > 0 ) {
//			discount = 10 * Float.parseFloat( fields[3] ) / Float.parseFloat( fields[2] );
//		}
//		
//		DecimalFormat df = new DecimalFormat( "########.0");
//		discount = Float.parseFloat( df.format( discount ) );
		
		String discount = Util.getDiscount( fields[3] , fields[2] );
		
		if ( Util.isValid( discount ) ) {
			fields[2] = discount + getContext().getString( R.string.discount_symbol );
		}
		else {
			fields[2] = "";
		}
		
		fields[3] = getContext().getString( R.string.chinese_yuan_symbol ) + fields[3];
		
		return fields;
	}
	
	private ListItemUnion[] getListItemUnion( String[] fields ) {
		ListItemUnion[] items = null;
		if ( fields != null && fields.length > 0 ) {
			items = new ListItemUnion[ fields.length ];
			
			if ( items != null ) {
				// the 1st field is for image URI
				items[0] = new ListItemUnion( R.drawable.unknown, fields[0] );
				
				for ( int i = 1; i < fields.length; i ++ ) {
					items[i] = new ListItemUnion( fields[i] );
				}
			}
		}
		
		return items;
	}
	
	protected void requestGrouponList( Bundle bundle ) {
		mBundle = bundle;
		Log.d( "MultiItemListLayout", "Desired page = " + String.valueOf( mDesiredPage ) + ", Total page = " + String.valueOf( mTotalPage ) );
		
		if ( mDesiredPage <= mTotalPage && !mLoadingInProgress ) {
			sendGrouponListRequest( bundle );
			mLoadingInProgress = true;
		}
	}
	
	private void sendGrouponListRequest( Bundle bundle ) {
		bundle.putInt( REQUEST.PARAM_PAGE, mDesiredPage );
		GrouponListRequestIntent request = new GrouponListRequestIntent();
		request.setSourceClass( getClass().getName() );
		request.putExtras( bundle );
		sendRequest( request );
	}
	
	protected BaseAdapter getAdapter() {
		return mAdapter;
	}
}
