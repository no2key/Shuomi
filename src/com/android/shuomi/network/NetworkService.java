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
	
	static private final int MSG_SEND = 0;
	static private final int MSG_ABORT = 1;
	
	private Looper mServiceLooper;
	private ServiceHandler mWorkHandler = null;
	private final IBinder mBinder = new LocalBinder();
	private NetworkObservable mResponseObservable = new NetworkObservable();
	private NetworkObservable mRequestObservable = new NetworkObservable();
	
	public void addRequestObserver( Observer observer ) {
		mRequestObservable.addObserver(observer);
	}
	
	private void notifyRequestObservers( Object data ) {
		mRequestObservable.notifyObservers( data );
	}
	
	public void addResponseObserver( Observer observer ) {
		mResponseObservable.addObserver(observer);
	}
	
//	public void removeResponseObservers() {
//		Bundle response = new Bundle();
//		response.putInt( PARAM.HTTP_RSP.STATUS, ResponseHandler.HTTP_ABORT );
//		mResponseObservable.notifyObservers( response );
//	}
	
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
		
		private HttpService mHttpService = null;
		
		public ServiceHandler( Looper looper ) {
			super( looper );
		}
	    
		@Override
	    public void handleMessage( Message msg ) {
			switch ( msg.arg1 ) {
			
			case MSG_SEND:
				notifyRequestObservers( String.valueOf( "request" ) );
				
				Bundle response = sendHttpGetRequest( ( String ) msg.obj );
				
				if ( Util.isValid( response.getString( PARAM.HTTP_RSP.BODY ) ) ) {
					Log.d( "NetworkService",  "STATUS: " + response.getInt( PARAM.HTTP_RSP.STATUS ) );
					Log.d( "NetworkService",  response.getString( PARAM.HTTP_RSP.BODY ) );
				}
				
				notifyResponseObservers( response );
				break;
			
			case MSG_ABORT:
				Log.w( "NetworkService", "processing abort message" );
				sendHttpAbortRequest();
				break;
			}
			
		}
		
		private Bundle sendHttpGetRequest( String url ) {
			mHttpService = new HttpService();
			mHttpService.get( url );
			
			Bundle bundle = new Bundle();
			bundle.putInt( PARAM.HTTP_RSP.STATUS, mHttpService.getLastStatus() );
			bundle.putString( PARAM.HTTP_RSP.BODY, mHttpService.getBody() );
			mHttpService = null;
			
			return bundle;
		}
		
		private void sendHttpAbortRequest() {
			if ( mHttpService != null ) {
				mHttpService.abortRequest();
				
				Bundle response = new Bundle();
				response.putInt( PARAM.HTTP_RSP.STATUS, ResponseHandler.HTTP_ABORT );
				mResponseObservable.notifyObservers( response );
				Log.d( "NetworkService", "send abort response" );
			}
			else {
				Log.d( "NetworkService", "mHttpService has been invalidated" );
			}
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
	
	private void sendRequestMessage( String url ) {
		Message msg = mWorkHandler.obtainMessage();
	    msg.obj = url;
	    msg.arg1 = MSG_SEND;
	    mWorkHandler.sendMessage( msg );
	}
	
	public void exec( RequestIntent request ) {
		RequestComposer composer = RequestCreator.create( request );
		sendRequestMessage( composer.getRequetUri() );
	}
	
	private void sendCancelMessage() {
		Message msg = mWorkHandler.obtainMessage();
	    msg.arg1 = MSG_ABORT;
	    mWorkHandler.sendMessage( msg );
	    Log.d( "NetworkService", "send abort message" );
	}

	public void cancelRequest() {
		sendCancelMessage();
	}
}
