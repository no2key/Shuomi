package com.android.shuomi;

import com.android.shuomi.util.EventIndicator;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;

import android.app.Application;
import android.util.Log;

public class ShuomiApp extends Application 
{
	public static ShuomiApp mShuomiApp;
	
	public BMapManager mBMapMan = null;
	public String mStrKey = "76473C77A5838461D53EF68DB430819F7C4711C4";
	@SuppressWarnings("unused")
	private boolean m_bKeyRight = true;
	
	static class MyGeneralListener implements MKGeneralListener 
	{
		@Override
		public void onGetNetworkState( int iError ) 
		{
			EventIndicator.showToast( ShuomiApp.mShuomiApp.getApplicationContext(), R.string.network_error );
		}

		@Override
		public void onGetPermissionState( int iError ) 
		{
			if ( iError ==  MKEvent.ERROR_PERMISSION_DENIED ) 
			{
				EventIndicator.showToast( ShuomiApp.mShuomiApp.getApplicationContext(), R.string.invalid_bmap_key );
				ShuomiApp.mShuomiApp.m_bKeyRight = false;
			}
		}
	}
	
	@Override
    public void onCreate() 
	{
		Log.d( "ShuomiApp", "onCreate" );
		mShuomiApp = this;
		mBMapMan = new BMapManager( this );
		mBMapMan.init( this.mStrKey, new MyGeneralListener() );
		super.onCreate();
	}
	
	@Override
	public void onTerminate() 
	{
		Log.d( "ShuomiApp", "onTerminate" );
		
		if ( mBMapMan != null ) 
		{
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}
}
