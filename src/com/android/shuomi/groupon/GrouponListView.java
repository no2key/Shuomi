package com.android.shuomi.groupon;

import com.android.shuomi.R;
import com.android.shuomi.intent.REQUEST;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class GrouponListView extends MultiItemListLayout {

	private String mReqAction = REQUEST.LIST_GROUPON;
	private Bundle mBundle = null;
	
	public GrouponListView( Context context, Bundle bundle ) {
		super(context);

		mBundle = bundle;
		setupTitle();
		requestGrouponList( mBundle );
		addListScrollListener();
		addListTouchListener();
	}
	
	private void addListTouchListener() {
		getList().setOnTouchListener( new OnTouchListener() {
			
			@Override
			public boolean onTouch( View v, MotionEvent event ) {
				final int y = (int) event.getY();
				//int action = event.getAction();
				//Log.e( "GrouponListView", "on touch event action = " + action );
				
		        switch (event.getAction()) {
		            case MotionEvent.ACTION_UP:
		            	if ( approachTop() ) {
			            	View top = getAdapter().getView( 1, null, null );
							int topY = getList().getChildAt( 1 ).getTop();
							int topY1 = top.getTop();
							Log.e( "GrouponListView", "ACTION_UP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!, top = " + String.valueOf( topY1 ) + ", " + String.valueOf( topY ) );
		            	}
		                break;
		            case MotionEvent.ACTION_DOWN:
		            	mLastMotionY = y;
		                break;
		            case MotionEvent.ACTION_MOVE:
		            	applyHeaderPadding( event );
		            	//Log.e( "GrouponListView", "ACTION MOVE, y = " + y );
		                break;
		        }
		        
				return false;
			}
		});
	}

	
	private void addListScrollListener() {
		getList().setOnScrollListener( new OnScrollListener() {

			@Override
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount ) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScrollStateChanged( AbsListView view, int scrollState ) {
				switch ( scrollState ) {
					
					case OnScrollListener.SCROLL_STATE_IDLE:
						
						Log.d( "GrouponListView", "Last = " + String.valueOf( view.getLastVisiblePosition() ) + 
								", Total = " + String.valueOf( view.getCount() ) );
						
//						if ( view.getLastVisiblePosition() == ( view.getCount() - 1 ) ) {
//							traceStatus( view );
//						}
						
						if ( reachBottom( view ) ) {
							Log.v( "GrouponListView", "reach the bottom 1" );
							onReachLastItem();
						}						
						else if ( reachTop( view ) ) {
							Log.v( "GrouponListView", "reach the top" );
							onReachFirstItem();
						}
						
						break;

					case OnScrollListener.SCROLL_STATE_FLING:
//						View top1 = getAdapter().getView( getList().getHeaderViewsCount(), null, null );
//						int topY = top1.getTop();
//						//int topY = view.getChildAt( getList().getHeaderViewsCount() ).getTop();
//						//Log.w( "GrouponListView", "FLING, top = " + topY );
						break;

					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//						View top = getAdapter().getView( getList().getHeaderViewsCount(), null, null );
//						//int topY1 = view.getChildAt( getList().getHeaderViewsCount() ).getTop();
//						int topY1 = top.getTop();
//						//Log.w( "GrouponListView", "TOUCH_SCROLL, top = " + topY1 );
						
						if ( reachTop( view ) ) {
							Log.v( "GrouponListView", "reach the top" );
							onReachFirstItem();
						}
						break;
				}
			}
		} );
	}
	
	private void traceStatus( AbsListView view ) {
		int lastPos = view.getLastVisiblePosition();
		Log.d( "GrouponListView", "View Height = " + String.valueOf( view.getHeight() ) );
		Log.d( "GrouponListView", "View Top = " + String.valueOf( view.getTop() ) );
		Log.d( "GrouponListView", "View Bottom = " + String.valueOf( view.getBottom() ) );
		Log.d( "GrouponListView", "Item Count = " +  String.valueOf( view.getCount() ) );
		//Log.d( "GrouponListView", "Item Bottom = " + String.valueOf( view.getChildAt(view.getCount() - 1).getBottom() ) );
		for ( int i = 0; i <= lastPos && i < getAdapter().getCount(); i ++ ) {
			//View bottom = view.getChildAt( i );
			View bottom = getAdapter().getView( i, null, null );
			if ( bottom != null ) {
				Log.d( "GrouponListView", String.valueOf( i ) + ". Item Bottom = " + String.valueOf( bottom.getBottom() ) );
			}
		}
		Log.v( "GrouponListView", "reach the bottom 2" );
	}
	
	private boolean approachTop() {
		int extra = getList().getHeaderViewsCount();
		Log.e( "GrouponListView", "header view count = " + extra );
		Log.e( "GrouponListView", "first visible = " + getList().getFirstVisiblePosition() );
		Log.e( "GrouponListView", "count = " + getList().getCount() );
		Log.e( "GrouponListView", "first bottom = " + getList().getChildAt(extra).getBottom() );
		Log.e( "GrouponListView", "first adapter = " + getAdapter().getView( extra, null, null ).getBottom() );
		
		return ( getList().getCount() > 0 && getList().getFirstVisiblePosition() == extra && 
				getAdapter().getView( extra, null, null ).getBottom() >= 50 ); 
	}
	
	private boolean reachBottom( AbsListView view ) { 
		int extra = getList().getHeaderViewsCount() + getList().getFooterViewsCount();
		return ( view.getLastVisiblePosition() == ( view.getCount() - 1 ) &&
				view.getHeight() - getAdapter().getView( view.getLastVisiblePosition() - extra, null, null ).getBottom() <= 1 );
	}
	
	private boolean reachTop( AbsListView view ) {
		return ( view.getCount() > 0 && view.getFirstVisiblePosition() == 0 && view.getChildAt(0).getTop() == 0 ); 
	}

	private void setupTitle()	{
		setTitleText( mBundle.getString( REQUEST.PARAM_CATE ) );
		setButtonText( R.string.hide_pic );
	}
	
	private void onReachFirstItem() {
		enableHeaderView( true );
	}
	
	private void onReachLastItem() {
		enableFooterView( true );
		requestGrouponList( mBundle );
	}

	@Override
	protected String getRequestAction() {
		return mReqAction;
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.groupon_list;
	}
}
