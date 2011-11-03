package com.android.shuomi.groupon;

import java.util.ArrayList;

import com.android.shuomi.R;
import com.android.shuomi.intent.GrouponListRequestIntent;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.parser.ResponseParser;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.AbsListView.OnScrollListener;

public abstract class MultiPageListLayout extends MultiItemListLayout {

	static final private String TAG = "MultiPageListLayout";
	
	private boolean mLoadingInProgress = false;
	private int mDesiredPage = 1;
	private int mTotalPage = 1;
	
	private View mFooter = null;
	private LinearLayout mFooterInnerView = null;
	private int mFooterInnerViewHeight = 0;
	private boolean mFooterEnabled = true;
	
	private Bundle mBundle = null;
	
	public MultiPageListLayout( Context context ) 
	{
		super( context );
		setupFooterView();
		addListScrollListener();
		addListTouchListener();
	}
	
	private void setupFooterView() {
		LayoutInflater inflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		mFooter = inflater.inflate( R.layout.list_loading_footer, getList(), false );
		
		mFooterInnerView = (LinearLayout) mFooter.findViewById( R.id.loading_more );
		mFooterInnerViewHeight = mFooterInnerView.getLayoutParams().height;

		enableFooterView( false );		
		getList().addFooterView( mFooter );
	}
	
	private void enableFooterView( boolean enable ) 
	{
		if ( enable != mFooterEnabled )
		{
			mFooterInnerView.getLayoutParams().height = enable ? mFooterInnerViewHeight : 0;
			mFooter.setVisibility( enable ? View.VISIBLE : View.GONE );
			
			if ( !mFooterEnabled )
			{
				getList().setSelection( getList().getCount() - 1 );
			}
			
			mFooterEnabled = enable;
		}
	}
	
	protected boolean moreToLoad()
	{
		return mDesiredPage <= mTotalPage && !mLoadingInProgress;
	}
	
	protected void updatePageInfoOnArrive( int totalPage )
	{
		mTotalPage = totalPage;
		mDesiredPage ++;
	}
	
	protected void requestGrouponList( Bundle bundle ) {
		mBundle = bundle;
		
		Log.d( TAG, "Desired page = " + mDesiredPage + ", Total page = " + mTotalPage );
		
		if ( mDesiredPage <= mTotalPage && !mLoadingInProgress ) {
			sendGrouponListRequest( bundle );
			enableFooterView( true );
			mLoadingInProgress = true;
		}
	}
	
	protected void requestUpdate( Bundle bundle )
	{
		if ( moreToLoad() )
		{
			mLoadingInProgress = true;
			sendGrouponListRequest( bundle );
		}
	}
	
	private void sendGrouponListRequest( Bundle bundle ) {
		bundle.putInt( REQUEST.PARAM_PAGE, mDesiredPage );
		GrouponListRequestIntent request = new GrouponListRequestIntent();
		request.setSourceClass( getClass().getName() );
		request.putExtras( bundle );
		sendRequest( request );
	}
	
	private void switchView() {
		findViewById( R.id.empty_view_loading ).setVisibility( View.GONE );
		getList().setVisibility( View.VISIBLE );
	}
	
	private void setFooterOnResponse()
	{
		Log.d( TAG, "Desired page = " + mDesiredPage + ", Total page = " + mTotalPage );
		
//		if ( mDesiredPage == mTotalPage  ) 
//		{
//			getList().removeFooterView( mFooter );
//			Log.d( TAG, "remove the footer view" );
//		}
//		else 
		{
			enableFooterView( false );
			Log.d( TAG, "disable the footer view" );
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update( String requestAction, int dataType, Object data, int pageCount ) 
	{
		if ( requestAction.equals( getRequestAction() ) ) 
		{
			if ( dataType == ResponseParser.TYPE_ARRAY_LIST )
			{
				updatePageInfoOnArrive( pageCount );
				setFooterOnResponse();
				appendListItems( ( ArrayList<String[]> ) data );
				switchView();
				
				mLoadingInProgress = false;
			}
		}
	}
	
	private boolean reachBottom( AbsListView view ) { 
		int extraCount = getList().getHeaderViewsCount() + getList().getFooterViewsCount();
		
		return ( getList().getLastVisiblePosition() == ( getList().getCount() - 1 ) &&
				getList().getHeight() - getAdapter().getView( getList().getLastVisiblePosition() - extraCount, null, null ).getBottom() <= 1 );
	}
	
	private void onReachLastItem() 
	{
		requestGrouponList( mBundle );
	}
	
	private void addListScrollListener() 
	{
		getList().setOnScrollListener( new OnScrollListener() 
		{
			@Override
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount ) 
			{

			}

			@Override
			public void onScrollStateChanged( AbsListView view, int scrollState ) 
			{
				switch ( scrollState ) 
				{
					
				case OnScrollListener.SCROLL_STATE_IDLE:
					
					if ( reachBottom( view ) ) {
						Log.v( TAG, "reach the bottom" );
						onReachLastItem();
					}
					
					break;
				}
			}
		} );
	}

	private void addListTouchListener() 
	{
		getList().setOnTouchListener( new OnTouchListener() 
		{
			@Override
			public boolean onTouch( View v, MotionEvent event ) 
			{
		        switch (event.getAction()) 
		        {
	            case MotionEvent.ACTION_DOWN:
	            	if ( reachBottom( getList() ) ) 
	            	{
						Log.v( TAG, "reach the bottom 1" );
						onReachLastItem();
					}
	                break;
		        }
		        
				return false;
			}
		});
	}
}
