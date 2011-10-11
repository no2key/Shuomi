package com.android.shuomi.groupon;

import com.android.shuomi.R;
import com.android.shuomi.intent.REQUEST;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
						if ( view.getLastVisiblePosition() == ( view.getCount() - 1 ) ) {
							int lastPos = view.getLastVisiblePosition();
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
							Log.v( "GrouponListView", "reach the bottom" );
							onReachLastItem();
						}
						else if ( view.getCount() > 0 && view.getFirstVisiblePosition() == 0 ) {
							Log.d( "GrouponListView", "Top = " + String.valueOf( view.getChildAt(0).getTop() ) );
							onReachFirstItem();
							Log.v( "GrouponListView", "reach the top" );
						}
						
						break;

//					case OnScrollListener.SCROLL_STATE_FLING:
//						break;
//
//					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//						break;
				}
			}
		} );
	}

	private void setupTitle()	{
		setTitleText( mBundle.getString( REQUEST.PARAM_CATE ) );
		setButtonText( R.string.hide_pic );
	}
	
	private void onReachFirstItem() {
		
	}
	
	private void onReachLastItem() {
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
