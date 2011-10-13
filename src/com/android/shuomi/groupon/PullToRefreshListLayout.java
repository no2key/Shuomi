package com.android.shuomi.groupon;

import com.android.shuomi.R;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.AbsListView.OnScrollListener;

public abstract class PullToRefreshListLayout extends ListLayout {

	abstract protected BaseAdapter getAdapter();
	abstract protected void requestGrouponList( Bundle bundle );
	
	private final String TAG = " PullToRefreshListLayout";
	protected Bundle mBundle = null;
	
	private View mFooter = null;
	private LinearLayout mFooterInnerView = null;
	private int mFooterInnerViewHeight = 0;
	
	private View mHeader = null;
	private LinearLayout mHeaderInnerView = null;
	private int mHeaderInnerViewHeight = 0;
	
	private int mLastMotionY;
	private int mRefreshViewHeight;
	private int mRefreshOriginalTopPadding;
	
	private int mCurrentScrollState;
	private int mRefreshState;
	
	private static final int TAP_TO_REFRESH = 1;
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	
	public PullToRefreshListLayout( Context context ) {
		super( context );
		
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		setupFooterView( layoutInflater );
		setupHeaderView( layoutInflater );
		
		mRefreshViewHeight = mHeader.getMeasuredHeight();
		mRefreshOriginalTopPadding = mHeader.getPaddingTop();
		mRefreshState = TAP_TO_REFRESH;
		addListScrollListener();
		addListTouchListener();
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
	
	protected View getFooterView() {
		return mFooter;
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
	
	private void resetHeader() {
        if ( mRefreshState != TAP_TO_REFRESH ) {
            mRefreshState = TAP_TO_REFRESH;

            resetHeaderPadding();
            enableHeaderView( false );
            

//            // Set refresh view text to the pull label
//            mRefreshViewText.setText(R.string.pull_to_refresh_tap_label);
//            // Replace refresh drawable with arrow drawable
//            mRefreshViewImage.setImageResource(R.drawable.ic_pulltorefresh_arrow);
//            // Clear the full rotation animation
//            mRefreshViewImage.clearAnimation();
//            // Hide progress bar and arrow.
//            mRefreshViewImage.setVisibility(View.GONE);
//            mRefreshViewProgress.setVisibility(View.GONE);
        }
    }
	
	private void resetHeaderPadding() {
        mHeader.setPadding(
        		mHeader.getPaddingLeft(),
                mRefreshOriginalTopPadding,
                mHeader.getPaddingRight(),
                mHeader.getPaddingBottom());
    }
	
	protected void applyHeaderPadding(MotionEvent ev) {
        final int historySize = ev.getHistorySize();
        int pointerCount = ev.getPointerCount();

        for (int h = 0; h < historySize; h++) {
            for (int p = 0; p < pointerCount; p++) {
                if ( mRefreshState == RELEASE_TO_REFRESH) 
                {
                    if (isVerticalFadingEdgeEnabled()) {
                        setVerticalScrollBarEnabled(false);
                    }

                    int historicalY = (int) ev.getHistoricalY( p, h );

                    // Calculate the padding to apply, we divide by 1.7 to
                    // simulate a more resistant effect during pull.
                    int topPadding = (int) (((historicalY - mLastMotionY)
                            - mRefreshViewHeight) / 1.7);

                    mHeader.setPadding(
                    		mHeader.getPaddingLeft(),
                            topPadding,
                            mHeader.getPaddingRight(),
                            mHeader.getPaddingBottom());
                }
            }
        }
    }

	
	private void addListScrollListener() {
		getList().setOnScrollListener( new OnScrollListener() {

			@Override
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount ) {
				if ( mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL && mRefreshState != REFRESHING) {
		            if ( firstVisibleItem == 0 ) {
		                //mRefreshViewImage.setVisibility(View.VISIBLE);
		                if ( ( mHeader.getBottom() > mRefreshViewHeight + 20 || mHeader.getTop() >= 0)
		                        && mRefreshState != RELEASE_TO_REFRESH ) {
//		                    mRefreshViewText.setText(R.string.pull_to_refresh_release_label);
//		                    mRefreshViewImage.clearAnimation();
//		                    mRefreshViewImage.startAnimation(mFlipAnimation);
		                    mRefreshState = RELEASE_TO_REFRESH;
		                } else if ( mHeader.getBottom() < mRefreshViewHeight + 20
		                        && mRefreshState != PULL_TO_REFRESH ) {
		                    //mRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
		                    if ( mRefreshState != TAP_TO_REFRESH ) {
		                        //mRefreshViewImage.clearAnimation();
		                        //mRefreshViewImage.startAnimation(mReverseFlipAnimation);
		                    }
		                    mRefreshState = PULL_TO_REFRESH;
		                }
		            } else {
		                //mRefreshViewImage.setVisibility(View.GONE);
		                resetHeader();
		            }
		        } else if (mCurrentScrollState == SCROLL_STATE_FLING
		                && firstVisibleItem == 0
		                && mRefreshState != REFRESHING) {
		            getList().setSelection(1);
		        }

			}

			@Override
			public void onScrollStateChanged( AbsListView view, int scrollState ) {
				mCurrentScrollState = scrollState;
				
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
							//onReachFirstItem();
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
	
	public void prepareForRefresh() {
        resetHeaderPadding();

//        mRefreshViewImage.setVisibility(View.GONE);
//        // We need this hack, otherwise it will keep the previous drawable.
//        mRefreshViewImage.setImageDrawable(null);
//        mRefreshViewProgress.setVisibility(View.VISIBLE);
//
//        // Set refresh view text to the refreshing label
//        mRefreshViewText.setText(R.string.pull_to_refresh_refreshing_label);

        mRefreshState = REFRESHING;
    }
	
	public void onRefreshComplete() {
        Log.d(TAG, "onRefreshComplete");

        resetHeader();

        // If refresh view is visible when loading completes, scroll down to
        // the next item.
        if ( mHeader.getBottom() > 0) {
            getList().invalidateViews();
            //setSelection(1);
        }
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

		            	if ( getList().getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING ) {
		                    if ( ( mHeader.getBottom() > mRefreshViewHeight || mHeader.getTop() >= 0 )
		                            && mRefreshState == RELEASE_TO_REFRESH) {
		                        // Initiate the refresh
		                        mRefreshState = REFRESHING;
		                        prepareForRefresh();
		                        //onRefresh();
		                        onRefreshComplete();
		                    } else if ( mHeader.getBottom() < mRefreshViewHeight
		                            || mHeader.getTop() < 0) {
		                        // Abort refresh and scroll down below the refresh view
		                        resetHeader();
		                        //setSelection(1);
		                    }
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

	private void onReachFirstItem() {
		enableHeaderView( true );
	}
	
	private void onReachLastItem() {
		enableFooterView( true );
		requestGrouponList( mBundle );
	}

}
