package com.android.shuomi.network;

import java.util.Observer;

import com.android.shuomi.intent.PARAM;
import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.request.RequestComposer;
import com.android.shuomi.request.RequestCreator;
import com.android.shuomi.util.Util;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public class NetworkService extends Service {
	private Looper mServiceLooper;
	private ServiceHandler mWorkHandler = null;
	private final IBinder mBinder = new LocalBinder();
	private NetworkObservable mResponseObservable = new NetworkObservable();
	private NetworkObservable mRequestObservable = new NetworkObservable();
	
	public void addRequestObserver(Observer observer) {
		mRequestObservable.addObserver(observer);
	}
	
	private void notifyRequestObservers( Object data ) {
		mRequestObservable.notifyObservers( data );
	}
	
	public void addResponseObserver(Observer observer) {
		mResponseObservable.addObserver(observer);
	}
	
	private void notifyResponseObservers( Object data ) {
		mResponseObservable.notifyObservers( data );
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	public class LocalBinder extends Binder {
		public NetworkService getService() {
            return NetworkService.this;
        }
    }
	
	private final class ServiceHandler extends Handler {
		public ServiceHandler( Looper looper ) {
			super( looper );
		}
	    
		@Override
	    public void handleMessage( Message msg ) {
			notifyRequestObservers( String.valueOf( "request" ) );
			
			Bundle response = sendHttpGetRequest( ( String ) msg.obj );
			
			if ( Util.isValid( response.getString( PARAM.HTTP_RSP.BODY ) ) ) {
				Log.d( "NetworkService",  "STATUS: " + response.getInt( PARAM.HTTP_RSP.STATUS ) );
				Log.d( "NetworkService",  response.getString( PARAM.HTTP_RSP.BODY ) );
			}
			
			notifyResponseObservers( response );
		}
	}
	
	@Override
	public void onCreate() {
		creatWorkThread();
	}
	
	private void creatWorkThread() {
		HandlerThread thread = new HandlerThread( "ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND );
		thread.start();

		mServiceLooper = thread.getLooper();
		mWorkHandler = new ServiceHandler( mServiceLooper );
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();		
		//mWorkHandler.getLooper().quit();
		Log.e( "NetworkService", "onDestroy" );
	}
	
	private Bundle sendHttpGetRequest( String url ) {
		HttpService httpService = new HttpService();
		httpService.get( url );
		
		Bundle bundle = new Bundle();
		bundle.putInt( PARAM.HTTP_RSP.STATUS, httpService.getLastStatus() );
		bundle.putString( PARAM.HTTP_RSP.BODY, httpService.getBody() );
		
		return bundle;
	}

	private void sendRequestMessage( String url ) {
		Message msg = mWorkHandler.obtainMessage();
	    msg.obj = url;
	    mWorkHandler.sendMessage( msg );
	}
	
	public void exec( RequestIntent request ) {
		RequestComposer composer = RequestCreator.create( request );
		sendRequestMessage( composer.getRequetUri() );
	}
}
