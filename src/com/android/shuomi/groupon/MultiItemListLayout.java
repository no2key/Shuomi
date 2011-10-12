package com.android.shuomi.groupon;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.android.shuomi.R;
import com.android.shuomi.intent.GrouponListRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.network.NetworkSession;
import com.android.shuomi.parser.ResponseParser;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public abstract class MultiItemListLayout extends ListLayout {

	private final String TAG = " MultiItemLayout ";
	private final int[] mItemFieldResIds = { R.id.demo_image, R.id.provider, R.id.discount, R.id.price, R.id.details };
	private final int mItemResId = R.layout.groupon_list_item;
	
	
	private boolean mLoadingInProgress = false;
	private int mDesiredPage = 1;
	private int mTotalPage = 1;
	private ArrayList<ListItemUnion[]> mDataList = null;
	private BaseAdapter mAdapter = null;
	
	private View mFooter = null;
	private LinearLayout mFooterInnerView = null;
	private int mFooterInnerViewHeight = 0;
	
	private View mHeader = null;
	private LinearLayout mHeaderInnerView = null;
	private int mHeaderInnerViewHeight = 0;
	
	public MultiItemListLayout(Context context) {
		super( context );
		
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		setupFooterView( layoutInflater );
		setupHeaderView( layoutInflater );
	}
	
	private void setupHeaderView( LayoutInflater inflater ) {
		mHeader = inflater.inflate( R.layout.list_loading_footer, getList(), false );
		
		mHeaderInnerView = (LinearLayout) mHeader.findViewById( R.id.loading_more );
		mHeaderInnerViewHeight = mHeaderInnerView.getLayoutParams().height;

		enableHeaderView( false );		
		getList().addHeaderView( mHeader );
	}
	
	protected void enableHeaderView( boolean enable ) {
		mHeaderInnerView.getLayoutParams().height = enable ? mHeaderInnerViewHeight : 0;
		mHeader.setVisibility( enable ? View.VISIBLE : View.GONE );
	} 
	
	private void setupFooterView( LayoutInflater inflater ) {
		mFooter = inflater.inflate( R.layout.list_loading_footer, getList(), false );
		
		mFooterInnerView = (LinearLayout) mFooter.findViewById( R.id.loading_more );
		mFooterInnerViewHeight = mFooterInnerView.getLayoutParams().height;

		enableFooterView( false );		
		getList().addFooterView( mFooter );
	}
	
	protected void enableFooterView( boolean enable ) {
		mFooterInnerView.getLayoutParams().height = enable ? mFooterInnerViewHeight : 0;
		mFooter.setVisibility( enable ? View.VISIBLE : View.GONE );
		
		if ( enable ) {
			getList().setSelection( getList().getCount() - 1 );
		}
	}
	
	private void switchView() {
		findViewById( R.id.empty_view_loading ).setVisibility( View.GONE );
		getList().setVisibility( View.VISIBLE );
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
			getList().removeFooterView( mFooter );
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
		float discount = 10 * Float.parseFloat( fields[3] ) / Float.parseFloat( fields[2] );
		DecimalFormat df = new DecimalFormat( "########.0");
		discount = Float.parseFloat( df.format( discount ) );
		fields[2] = Float.toString( discount ) + getContext().getString( R.string.discount_symbol );
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
		Log.d( "MultiItemListLayout", "Desired page = " + String.valueOf( mDesiredPage ) + ", Total page = " + String.valueOf( mTotalPage ) );
		
		if ( mDesiredPage <= mTotalPage && !mLoadingInProgress ) {
			sendGrouponListRequest( bundle );
			mLoadingInProgress = true;
		}
	}
	
	private void sendGrouponListRequest( Bundle bundle ) {
		bundle.putInt( REQUEST.PARAM_PAGE, mDesiredPage );
		GrouponListRequestIntent request = new GrouponListRequestIntent();
		request.putExtras( bundle );
		NetworkSession.send( request );
	}
	
	protected BaseAdapter getAdapter() {
		return mAdapter;
	}
}
