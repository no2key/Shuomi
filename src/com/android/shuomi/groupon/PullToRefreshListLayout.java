package com.android.shuomi.groupon;

import com.android.shuomi.R;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.widget.TextView;

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
	private boolean mHeaderEnabled;
	
	private int mLastMotionY;
	private int mRefreshViewHeight;
	private int mRefreshOriginalTopPadding;
	
	private int mCurrentScrollState;
	private int mRefreshState;
	
	private RotateAnimation mFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;
	
	private static final int TAP_TO_REFRESH = 1;
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	
	private static final int PULL_RELEASE_THRESHOLD = 20;
	
	private TextView mHeaderText;
    private ImageView mHeaderImage;
    private ProgressBar mHeaderProgress;
    private TextView mHeaderLastUpdated;
	
	public PullToRefreshListLayout( Context context ) {
		super( context );
		
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		setupFooterView( layoutInflater );
		setupHeaderView( layoutInflater );
		
		//mRefreshState = TAP_TO_REFRESH;
		setRefreshState( TAP_TO_REFRESH );
		addListScrollListener();
		addListTouchListener();
		setupAnimations();
		getHeaderWidgets();
	}
	
	private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        
        if ( p == null ) {
            p = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec( 0, 0 + 0, p.width );
        int lpHeight = p.height;
        int childHeightSpec;
        
        if ( lpHeight > 0 ) {
            childHeightSpec = MeasureSpec.makeMeasureSpec( lpHeight, MeasureSpec.EXACTLY );
        } 
        else {
            childHeightSpec = MeasureSpec.makeMeasureSpec( 0, MeasureSpec.UNSPECIFIED );
        }
        child.measure( childWidthSpec, childHeightSpec );
    }
	
	private void getHeaderWidgets() {
		mHeaderText = ( TextView ) mHeader.findViewById( R.id.pull_to_refresh_text );
        mHeaderImage = ( ImageView ) mHeader.findViewById( R.id.pull_to_refresh_image );
        mHeaderProgress = ( ProgressBar ) mHeader.findViewById( R.id.pull_to_refresh_progress );
        mHeaderLastUpdated = ( TextView ) mHeader.findViewById( R.id.pull_to_refresh_updated_at );
        
        mHeaderImage.setMinimumHeight(50);
	}
	
	private void setupAnimations() {
        mFlipAnimation = new RotateAnimation( 0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f );
        mFlipAnimation.setInterpolator( new LinearInterpolator() );
        mFlipAnimation.setDuration( 250 );
        mFlipAnimation.setFillAfter( true );
        
        mReverseFlipAnimation = new RotateAnimation( -180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f );
        mReverseFlipAnimation.setInterpolator( new LinearInterpolator() );
        mReverseFlipAnimation.setDuration( 250 );
        mReverseFlipAnimation.setFillAfter( true );
	}
	
	public void setLastUpdated( CharSequence lastUpdated ) {
        if (lastUpdated != null) {
        	mHeaderLastUpdated.setVisibility( View.VISIBLE );
        	mHeaderLastUpdated.setText( lastUpdated );
        } 
        else {
        	mHeaderLastUpdated.setVisibility( View.GONE );
        }
    }
	
	private void setupHeaderView( LayoutInflater inflater ) {
		mHeader = inflater.inflate( R.layout.pull_to_refresh_header, getList(), false );
		
		mHeaderInnerView = ( LinearLayout ) mHeader.findViewById( R.id.pull_to_refresh_header );
		mHeaderInnerViewHeight = mHeaderInnerView.getLayoutParams().height;

		measureView( mHeader );
		mRefreshViewHeight = mHeader.getMeasuredHeight();
		Log.e( TAG, "SETUP Header View height = " + mRefreshViewHeight );
		
		mRefreshOriginalTopPadding = mHeader.getPaddingTop();
		Log.e( TAG, "Header View top padding = " + mRefreshOriginalTopPadding );
		
		enableHeaderView( false );		
		getList().addHeaderView( mHeader );
	}
	
	protected void enableHeaderView( boolean enable ) {
		mHeaderEnabled = enable;
		mHeaderInnerView.getLayoutParams().height = mHeaderEnabled ? mHeaderInnerViewHeight : 0;
		mHeader.setVisibility( mHeaderEnabled ? View.VISIBLE : View.GONE );
	}
	
	private boolean isHeaderEnabled() {
		return mHeaderEnabled;
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
            //mRefreshState = TAP_TO_REFRESH;
            setRefreshState( TAP_TO_REFRESH );
            Log.e( TAG, "mCurrentScrollState = " + mCurrentScrollState );
            mCurrentScrollState = OnScrollListener.SCROLL_STATE_IDLE;
            
            resetHeaderPadding();
            enableHeaderView( false );            
            resetHeaderWidgets();
        }
    }
	
	private void resetHeaderWidgets() {
      mHeaderText.setText( R.string.pull_to_refresh_tap_label );
      mHeaderImage.setImageResource( R.drawable.ic_pulltorefresh_arrow );
      mHeaderImage.clearAnimation();
      mHeaderImage.setVisibility( View.GONE );
      mHeaderProgress.setVisibility( View.GONE );
	}
	
	private void resetHeaderPadding() {
        mHeader.setPadding(	mHeader.getPaddingLeft(), mRefreshOriginalTopPadding,
                mHeader.getPaddingRight(), mHeader.getPaddingBottom() );
    }
	
	protected void applyHeaderPadding(MotionEvent ev) {
        final int historySize = ev.getHistorySize();
        int pointerCount = ev.getPointerCount();

        for (int h = 0; h < historySize; h++) {
            for (int p = 0; p < pointerCount; p++) {
                if ( mRefreshState == RELEASE_TO_REFRESH || mRefreshState == PULL_TO_REFRESH || mRefreshState == REFRESHING ) {
                    if (isVerticalFadingEdgeEnabled()) {
                        setVerticalScrollBarEnabled(false);
                    }

                    int historicalY = (int) ev.getHistoricalY( p, h );

                    // Calculate the padding to apply, we divide by 1.7 to
                    // simulate a more resistant effect during pull.
                    int topPadding = (int) (((historicalY - mLastMotionY)
                            - mRefreshViewHeight) / 1.7);

                    mHeader.setPadding( mHeader.getPaddingLeft(), topPadding, 
                    		mHeader.getPaddingRight(), mHeader.getPaddingBottom() );
                }
            }
        }
    }

	private void releaseToRefresh() {
		mHeaderText.setText( R.string.pull_to_refresh_release_label );
        mHeaderImage.clearAnimation();
        mHeaderImage.startAnimation( mFlipAnimation );
        
        //mRefreshState = RELEASE_TO_REFRESH;
        setRefreshState( RELEASE_TO_REFRESH );
	}
	
	private boolean pullBeyondBound() {
		return ( mHeader.getBottom() > mRefreshViewHeight + PULL_RELEASE_THRESHOLD /*|| mHeader.getTop() >= 0*/ );
	}
	
	private boolean releaseBackInBound() {
		return ( mHeader.getBottom() < mRefreshViewHeight + PULL_RELEASE_THRESHOLD );
	}
	
	private void addListScrollListener() {
		getList().setOnScrollListener( new OnScrollListener() {

			@Override
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount ) {
				if ( mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL /*&& mRefreshState != REFRESHING*/ ) {
		            if ( firstVisibleItem == 0 ) {
		                mHeaderImage.setVisibility( View.VISIBLE );
		                
//		                Log.e( TAG, "header top = " + mHeader.getTop() + ", bottom = " + mHeader.getBottom() );
		                
		                
		                if ( mRefreshState == REFRESHING ) {
		                	//mHeaderImage.setVisibility( View.VISIBLE );
		                }
		                else if ( releaseBackInBound() && mRefreshState != PULL_TO_REFRESH ) {
		                	if ( !isHeaderEnabled() ) {
		                		enableHeaderView( true );
		                	}
		                	
		                	mHeaderText.setText( R.string.pull_to_refresh_pull_label );
		                    
		                    if ( mRefreshState != TAP_TO_REFRESH ) {
		                        mHeaderImage.clearAnimation();
		                        mHeaderImage.startAnimation( mReverseFlipAnimation );
		                    }
		                    //mRefreshState = PULL_TO_REFRESH;
		                    Log.e( TAG, "release back in bound" );
		                    setRefreshState( PULL_TO_REFRESH );
		                }
		                else if ( pullBeyondBound() && mRefreshState != RELEASE_TO_REFRESH ) {
		                	releaseToRefresh();
		                }
		            } 
		            else {
		                mHeaderImage.setVisibility( View.GONE );
		                resetHeader();
		            }
		        } 
				else if ( mCurrentScrollState == SCROLL_STATE_FLING 
		                && firstVisibleItem == 0
		                && mRefreshState != REFRESHING ) {
		            getList().setSelection(1);
		        }

			}

			@Override
			public void onScrollStateChanged( AbsListView view, int scrollState ) {
				mCurrentScrollState = scrollState;
				
				switch ( scrollState ) {
					
					case OnScrollListener.SCROLL_STATE_IDLE:
						
						if ( reachBottom( view ) ) {
							Log.v( "GrouponListView", "reach the bottom 1" );
							onReachLastItem();
						}						
//						else if ( reachTop( view ) ) {
//							Log.v( "GrouponListView", "reach the top" );
//							onReachFirstItem();
//						}
						
						break;

					case OnScrollListener.SCROLL_STATE_FLING:
//						View top1 = getAdapter().getView( getList().getHeaderViewsCount(), null, null );
//						int topY = top1.getTop();
//						//int topY = view.getChildAt( getList().getHeaderViewsCount() ).getTop();
//						//Log.w( "GrouponListView", "FLING, top = " + topY );
//						if ( reachTop( view ) ) {
//							Log.v( "GrouponListView", "reach the top" );
//							onReachFirstItem();
//						}
						break;

					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//						View top = getAdapter().getView( getList().getHeaderViewsCount(), null, null );
//						//int topY1 = view.getChildAt( getList().getHeaderViewsCount() ).getTop();
//						int topY1 = top.getTop();
//						//Log.w( "GrouponListView", "TOUCH_SCROLL, top = " + topY1 );
						
//						if ( reachTop( view ) ) {
//							Log.v( "GrouponListView", "reach the top" );
//							onReachFirstItem();
//						}
						break;
				}
			}
		} );
	}
	
	private void addListTouchListener() {
		getList().setOnTouchListener( new OnTouchListener() {
			
			@Override
			public boolean onTouch( View v, MotionEvent event ) {
				final int y = (int) event.getY();
				
		        switch (event.getAction()) {
		            case MotionEvent.ACTION_UP:
		            case MotionEvent.ACTION_DOWN:
		            	
//		            	if ( approachTop() ) {
//			            	View top = getAdapter().getView( 1, null, null );
//							int topY = getList().getChildAt( 1 ).getTop();
//							int topY1 = top.getTop();
//							Log.e( "GrouponListView", "ACTION_UP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!, top = " + String.valueOf( topY1 ) + ", " + String.valueOf( topY ) );
//		            	}

		            	if ( getList().getFirstVisiblePosition() == 0 /*&& mRefreshState != REFRESHING*/ ) {
		            		if ( mRefreshState == REFRESHING ) {
		            			//resetHeaderPadding();
		            			prepareForRefresh();
		            		}
		            		else if ( ( mHeader.getBottom() > mRefreshViewHeight || mHeader.getTop() >= 0 )
		                            && mRefreshState == RELEASE_TO_REFRESH ) {
		                        // Initiate the refresh
		                        //mRefreshState = REFRESHING;
		                        setRefreshState( REFRESHING );
		                        prepareForRefresh();
		                        onRefresh();
		                        //onRefreshComplete();
		                    } 
		                    else if ( mHeader.getBottom() < mRefreshViewHeight || mHeader.getTop() < 0) {
		                        // Abort refresh and scroll down below the refresh view
		                        resetHeader();
		                        getList().setSelection(1);
		                    }
		                }

//		                break;
//		            case MotionEvent.ACTION_DOWN:
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
	
	private void onRefresh() {
		
	}
	
	public void prepareForRefresh() {
        resetHeaderPadding();

        mHeaderImage.setVisibility( View.GONE );
        // We need this hack, otherwise it will keep the previous drawable.
        mHeaderImage.setImageDrawable( null );
        mHeaderProgress.setVisibility( View.VISIBLE );

        // Set refresh view text to the refreshing label
        mHeaderText.setText( R.string.pull_to_refresh_refreshing_label );

        //mRefreshState = REFRESHING;
        setRefreshState( REFRESHING );
    }
	
	public void onRefreshComplete() {
        Log.d(TAG, "onRefreshComplete");

        resetHeader();

        // If refresh view is visible when loading completes, scroll down to
        // the next item.
        if ( mHeader.getBottom() > 0) {
            getList().invalidateViews();
            getList().setSelection(1);
        }
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

	private void setRefreshState( int state ) {
		String[] status = { "NULL", "TAP_TO_REFRESH", "PULL_TO_REFRESH", "RELEASE_TO_REFRESH", "REFRESHING" };
		Log.e( TAG, "old state = " + status[mRefreshState] + ", new state = " + status[state] );
		mRefreshState = state;
	}
}
