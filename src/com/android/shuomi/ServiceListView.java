package com.android.shuomi;

import com.android.shuomi.favorites.FavoritesListView;
import com.android.shuomi.groupon.GrouponMainView;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.intent.ResponseIntent;
import com.android.shuomi.network.NetworkBindActivity;
import com.android.shuomi.network.NetworkResponse;
import com.android.shuomi.network.NetworkResponseHandler;
import com.android.shuomi.network.NetworkSession;
import com.android.shuomi.parser.ResponseParser;
import com.android.shuomi.parser.ResponseParserCreator;
import com.android.shuomi.persistence.DatabaseSession;
import com.android.shuomi.util.EventIndicator;
import com.android.shuomi.util.Util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class ServiceListView extends NetworkBindActivity implements NetworkResponseHandler {
	
	private static final String TAG = "ServiceListView";
	private final String mTag = "tab";
	private final int mTabLabels[] = { R.string.groupon_info, R.string.my_favourite, R.string.utility_list };
	private final int mTabResIds[] = { R.id.tab1, R.id.tab2, R.id.tab3 };	
	private ViewFlipper[] mFlippers;// = new ViewFlipper[3];
	
	private final String PROVINCE_SELECTED = "PROVINCE_SELECTED";
	private final String CITY_SELECTED = "CITY_SELECTED";
	private String mProvinceSelected = null;
	private String mCitySelected = null;
	private TabHost mTabHost;
	
	////////////////////////////
	// Initialize Activity
	////////////////////////////
	
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.service_list );
		
        createTab();
        loadSelectedLocation();        
        initFlipper();
        DatabaseSession.create( this );
    }
	
	private void createTab() {
		mTabHost = ( TabHost ) findViewById( R.id.tabhost );
		mTabHost.setup( this.getLocalActivityManager() );
        
        for ( int i = 0; i < mTabLabels.length; i ++ ) {
        	mTabHost.addTab( mTabHost.newTabSpec( mTag + String.valueOf( i+1 ) )
                   .setIndicator( getString( mTabLabels[i] ) ) 
                   .setContent( mTabResIds[i] ) );
        }
        
        final TabWidget tabWidget = mTabHost.getTabWidget();
        tabWidget.setStripEnabled( false );
        setupTabLabelProperty( tabWidget );
        
        int tabHeight = tabWidget.getChildTabViewAt( 0 ).getLayoutParams().height;
        mTabHost.getTabContentView().setPadding( 0, 0, 0, tabHeight );
        
        mTabHost.setCurrentTab( 0 );
        mTabHost.setOnTabChangedListener( new OnTabChangeListener() {
			
			@Override
			public void onTabChanged( String tabId ) {
				onTabChange(  tabId.charAt( tabId.length()-1 ) - '1' );
			}
		});
	}
	
	private void onTabChange( int index ) {
		Log.d( TAG, "index = " + index );
		if ( index == 1 ) {
			setupFavoriteFlipper();
		}
	}
	
	private void setupFavoriteFlipper() {
		if ( mFlippers[1] == null ) {
			mFlippers[1] = ( ViewFlipper ) findViewById( mTabResIds[1] );
			goToNextView( new FavoritesListView( this ) );
		}
	}
	
	private void setupTabLabelProperty( final TabWidget tabWidget ) {
        for ( int i = 0; i < tabWidget.getChildCount(); i ++ ) {
        	View view = tabWidget.getChildTabViewAt( i );   
        	//view.getLayoutParams().height = 60; //tabWidget.getChildAt(i)
        	final TextView tv = ( TextView ) view.findViewById( android.R.id.title );
        	tv.setTextSize( 17 );
        	tv.setTextColor( this.getResources().getColorStateList( android.R.color.white ) );
        }
	}
	
	private void initFlipper() {
		mFlippers = new ViewFlipper[mTabResIds.length];
		mFlippers[0] = ( ViewFlipper ) findViewById( mTabResIds[0] );
		
		goToNextView( new GrouponMainView( this, mProvinceSelected, mCitySelected ) );
		
		mFlippers[1] = null;
		mFlippers[2] = null;
	}
	
	////////////////////////////
	// Do BACK key magic
	////////////////////////////
	
	public boolean onKeyDown( int keyCode, KeyEvent event ) {
		boolean handled = true;
		
		if ( ( keyCode == KeyEvent.KEYCODE_BACK ) && flipperGoBack() ) {
			handled = true;
        }
		else {
			handled = super.onKeyDown( keyCode, event );
		}
        
        return handled;
	}
	
	private ViewFlipper getCurrentFlipper() {
		return mFlippers[mTabHost.getCurrentTab()];
	}
	
	private boolean flipperGoBack() {
		boolean canBack = false;		
		ViewFlipper current = getCurrentFlipper();

		if ( current != null && current.getDisplayedChild() > 0 ) {
			try {
				BackableView child = (BackableView) current.getCurrentView();
				canBack = child.back();
			}
			catch ( ClassCastException e ) {
				Log.w( TAG, e.getMessage() );
			}
			
			if ( !canBack ) {
				current.removeViewAt( current.getDisplayedChild() );
				Log.v( "ServiceListView", "current flipper index = " + String.valueOf( current.getDisplayedChild() ) );
				canBack = true;
			}
		}
		
		return canBack;
	}
	
	////////////////////////////
	// Sub-view switching 
	////////////////////////////
	
	public void goToNextView( View view ) {
		ViewFlipper current = getCurrentFlipper();
		current.addView( view );
		current.showNext();
	}
	
	@Override
	protected void onNetworkServiceConnected() {
		NetworkSession.create( this, getNetworkService() );		
	}
	
	public void onCitySelected( String province, String city ) {
		mProvinceSelected = province;
		mCitySelected = city;
		saveSelectedLocation();
		
		ViewFlipper current = getCurrentFlipper();
		
		if ( current != null ) {
			current.removeViewAt( current.getDisplayedChild() );
		}
		
		GrouponMainView view = ( GrouponMainView ) getCurrentFlipper().getCurrentView();
		view.setLocation( mProvinceSelected, mCitySelected );
	}
	
	private void loadSelectedLocation() {
		SharedPreferences pref = getSharedPreferences ( "groupon", 0 );
		mProvinceSelected = pref.getString(  PROVINCE_SELECTED, null );
		mCitySelected = pref.getString(  CITY_SELECTED, null );
		
		if ( !Util.isValid( mProvinceSelected ) ) {
			String defLocation = getString( R.string.allover );
			mProvinceSelected = defLocation;
			mCitySelected = defLocation;
		}
	}
	
	private void saveSelectedLocation() {
		if ( Util.isValid( mProvinceSelected ) && Util.isValid( mCitySelected ) ) {
			SharedPreferences pref = getSharedPreferences ( "groupon", 0 );
			Editor editor = pref.edit();
			editor.putString( PROVINCE_SELECTED,  mProvinceSelected );
			editor.putString( CITY_SELECTED,  mCitySelected );
			editor.commit();
		}
	}
	
	/////////////////////////////
	// Process network response 
	/////////////////////////////
	
	@Override
	protected void onNewIntent( Intent intent ) {
		if ( intent.getAction().equals( RESPONSE.HTTP_RESPONSE_GROUPON ) || 
			 intent.getAction().equals( RESPONSE.HTTP_RESPONSE_GROUPON_SHARE ) || 
			 intent.getAction().equals( RESPONSE.HTTP_RESPONSE_GROUPON_FAVORITE ) ) {
			ResponseIntent response = new ResponseIntent( intent );
			NetworkResponse.process( this, ( ResponseIntent ) response );
		}
	}
	
	@Override
	public void onResponse( ResponseIntent response ) {
		try {
			NetworkRequestLayout view = (NetworkRequestLayout) findDestinationView( response );
			
			if ( view != null ) {
				view.onRequestDone();
			}
		}
		catch( ClassCastException e ){
			Log.w( TAG, "Cast failed: NOT a NetworkRequestLayout!" );
		}
	}
	
	@Override
	public void onPositiveResponse( ResponseIntent response ) {
		if ( response.getAction().equals( RESPONSE.HTTP_RESPONSE_GROUPON ) ) {
			updateView( response );
		}
		else if ( response.getAction().equals( RESPONSE.HTTP_RESPONSE_GROUPON_SHARE ) ) {
			EventIndicator.showToast( this, getString( R.string.email_sent_done ) );
		}
		else if ( response.getAction().equals( RESPONSE.HTTP_RESPONSE_GROUPON_FAVORITE ) ) {
			EventIndicator.showToast( this, getString( R.string.favorite_added_done ) );
		}
	}

	@Override
	public void onNegativeResponse( int error, String message ) {
		// TODO Auto-generated method stub		
	}
	
	private void updateView( ResponseIntent response ) {
		UpdatableView view = findDestinationView( response );
		
		if ( view != null ) {
			ResponseParser parser = ResponseParserCreator.create( response );
			
			if ( parser.getDataType() == ResponseParser.TYPE_ARRAY ) {
				view.update( response.getRequestAction(), parser.getDataType(), parser.getData(), parser.getPageCount() );
			}
			else if ( parser.getDataType() == ResponseParser.TYPE_ARRAY_LIST ) {
				view.update( response.getRequestAction(), parser.getDataType(), parser.getDataList(), parser.getPageCount() );
			}
		}
		else {
			Log.e( TAG, "Do NOT find destination view!" );
		}
	}
	
	public Bundle getSelectedLocation() {
		Bundle bundle = new Bundle();
		bundle.putString( REQUEST.PARAM_PROVINCE , mProvinceSelected );
		bundle.putString( REQUEST.PARAM_CITY , mCitySelected );
		return bundle;
	}
	
	private UpdatableView findDestinationView( ResponseIntent response ) {
		UpdatableView dest = null;
		ViewFlipper flipper = null;
		
		if ( response.getAction().equals( RESPONSE.HTTP_RESPONSE_GROUPON ) ) {
			flipper = mFlippers[0];
		}
		
		if ( flipper != null ) {
			try {
				dest = (UpdatableView) findViewInFlipperByName( flipper, response.getSourceClass() );
			}
			catch( ClassCastException e ) {
				Log.w( TAG, "destination view is NOT an UpdatableView!" );
			}
		}
		
		return dest;
	}
	
	private View findViewInFlipperByName( ViewFlipper flipper, String className ) {
		View target = null;
		
		if ( flipper != null && Util.isValid( className ) ) {
			for ( int i = flipper.getChildCount() -1; i > -1; i -- ) {
				View view = flipper.getChildAt( i );
				if ( view != null && view.getClass().getName().equals( className ) ) {
					target = view;
				}
			}
		}
		
		return target;
	}
}
