package com.android.shuomi.network;

import com.android.shuomi.network.NetworkService.LocalBinder;
import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public abstract class NetworkBindActivity extends MapActivity {
	abstract protected void onNetworkServiceConnected();
	
	private NetworkService mNetworkService;
    private boolean mBound = false;
    
	private ServiceConnection mConnection = new ServiceConnection() {
    	
        public void onServiceConnected( ComponentName className, IBinder service ) {
            LocalBinder binder = (LocalBinder) service;
            mNetworkService = binder.getService();
            mBound = true;
            Log.v( "NetworkBindView", "NetworkSerivce bound" );
            
            onNetworkServiceConnected();
        }

        public void onServiceDisconnected( ComponentName arg0 ) {
        	//Log.v( "NetworkBindView", "NetworkSerivce unbound" );
            mBound = false;
        }
    };
    
    @Override
    protected void onStart() {
        super.onStart();
        
        Intent intent = new Intent( this, NetworkService.class );
        boolean i = bindService( intent, mConnection, Context.BIND_AUTO_CREATE );
        Log.v( "NetworkBindView", "bindService = " + String.valueOf(i) );
    }

    @Override
    protected void onStop() {
        super.onStop();

        if ( mBound ) {
            unbindService( mConnection );
            mBound = false;
        }
    }
    
    protected final NetworkService getNetworkService() {
    	return mNetworkService;
    }
}