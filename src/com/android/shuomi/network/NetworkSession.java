package com.android.shuomi.network;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.intent.ResponseIntent;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class NetworkSession implements Observer {
	
	private NetworkService mNetworkService = null;
	private Context mContext = null;
	private RequestIntent mLastProcess = null;	
	private BlockingQueue <RequestIntent> m_ProcessQueue = new LinkedBlockingQueue<RequestIntent>();
	
	static private NetworkSession mThis = null;

	static private final String TAG = "NetworkSession";
	
	static public NetworkSession create( Context context, NetworkService nwService ) {
		if ( mThis == null ) {
			mThis = new NetworkSession( context, nwService );
		}
		else {
			mThis.init( context, nwService );
		}
		return mThis;
	}
	
	static public NetworkSession getInstance() {
		return mThis;
	}
	
	static public void send( RequestIntent request ) {
		if ( mThis != null ) {
			mThis.processRequest( request );
		}
	}
	
	static public void cancel( int intentHashCode ) {
		if ( mThis != null ) {
			mThis.cancelRequest( intentHashCode );
		}
		
	}
	
	private NetworkSession( Context context, NetworkService nwService ) {
		init( context, nwService );
	}
	
	private void init( Context context, NetworkService nwService ) {
		mNetworkService = nwService;
		mContext = context;
	}
	
	private boolean isReady() {		
		return ( mNetworkService != null && mContext != null );
	}
	
	////////////////////
	// Processing Queue
	////////////////////
	
	private void appendProcessing( RequestIntent request ) {
		m_ProcessQueue.add( request );
		mLastProcess = request;
	}
	
	private RequestIntent obtainAndRemoveHeadProcessing() {
		return ( m_ProcessQueue.isEmpty() ) ? mLastProcess : m_ProcessQueue.remove();
	}
	
	////////////////////
	// Process Request
	////////////////////
	
//	private boolean toSaveResponse( RequestIntent request ) {
//		return request.getAction().equals( RequestIntent.request_retrieve );
//	}
//	
//	private FeatureBlock getFeatureBlock( RequestIntent request ) {
//		FeatureBlock block = null;
//		
//		if ( request != null ) {
//			ServiceIntent intent = (ServiceIntent) request;
//			block = ServiceManager.getFeatureBlock( intent.getService(), intent.getFeature() );
//		}
//		
//		return block;
//	}
//	
//	private boolean validate( RequestIntent request ) {
//		boolean checkPassed = true;
//		
//		if ( toSaveResponse( request ) ) {
//			checkPassed = ( getFeatureBlock( request ) != null );
//		}
//		
//		return checkPassed;
//	}
	
	private void handleNetworkService( RequestIntent request ) {
		appendProcessing( request );
		mNetworkService.addResponseObserver( this );
		mNetworkService.exec( request );
	}
	
	private void processRequest( RequestIntent request )  {
		if ( isReady() ) {
			handleNetworkService( request );
		}
	}
	
	private void cancelRequest( int intentHashCode ) {
		RequestIntent request = m_ProcessQueue.peek();
		
		if ( request != null && request.hashCode() == intentHashCode ) {
			Log.d( TAG, "request hashCode = " + request.hashCode() + ", intent's = " + intentHashCode );
			m_ProcessQueue.remove();
			mLastProcess = null;
			//mNetworkService.removeResponseObservers();
			mNetworkService.cancelRequest();
		}
	}
	
	////////////////////
	// Process Response
	////////////////////
	
	public synchronized void update( Observable observable, Object data ) {
		try {
			Log.d( TAG, "receive http response by observer" );
			Bundle response = ( Bundle ) data;
			ResponseIntent intent = ResponseHandler.process( response, obtainAndRemoveHeadProcessing() );
			
			if ( intent != null ) {
				mContext.startActivity( intent );
			}
		}
		catch ( ClassCastException e ) {
			Log.w( TAG, e.getMessage() );
		}
	}
	
//	private void processResponse( int status, String body, String cookie ) {
//		RequestIntent request = obtainAndRemoveHeadProcessing();
//		
//		if ( status == HTTP_OK ) {
//			processReponseBody( body, cookie, request );
//		}
//		else {
//			sendResultIntent( request, false, status, null );
//		}
//	}
	
//	private void saveResponse( ResponseParser parser, RequestIntent request, String cookie ) {
//		if ( parser.getResult() ) {
//			if ( toSaveResponse( request ) ) {
//				persistent( parser, request );
//			}
//			
//			setSessionCookie( cookie );
//		}
//	}
//	
//	private void processReponseBody( String body, String cookie, RequestIntent request ) {
//		if ( Util.isValid( body ) && request != null ) {
//			ResponseParser parser = ResponseParseFactory.createParser( body, request );
//			
//			if ( parser != null ) {				
//				saveResponse( parser, request, cookie );
//				
//				boolean result = parser.getResult();
//				int errorNo = ( result ) ? 0 : ResponseIntent.ERROR_WITH_MESSAGE;
//				String errorMessage = ( result ) ? null : parser.getMessage();
//				sendResultIntent( request, result, errorNo, errorMessage );
//			}			
//		}
//		else {
//			Log.e( TAG, "invalid response" );
//		}
//	}
//	
//	private void persistent( ResponseParser parser, RequestIntent request ) {
//		ServiceIntent intent = (ServiceIntent) request;
//		String service = intent.getService();
//		String feature = intent.getFeature();
//		DatabaseSessionEx dbSession = DatabaseSessionEx.getInstance();
//		
//		if ( dbSession != null ) {
//			if ( parser.isMultiPage() ) {
//				dbSession.save( service, feature, parser.getJsonData() );
//			}
//			else {
//				ArrayList<String[]> list = parser.getDataList();
//				dbSession.save( service, feature, null, list, true );
//			}
//		}
//	}
//
//	private ResponseIntent createResponseIntent( RequestIntent reqIntent, boolean result, int errorNo, String errorMessage ) {
//		ResponseIntent responseIntent = new ResponseIntent( reqIntent.getResponseAction() );
//		responseIntent.putId( reqIntent.getId() );
//		responseIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
//		responseIntent.setResult( result );
//		
//		try {
//			MultiPageRetrieveIntent intent = ( MultiPageRetrieveIntent ) reqIntent;
//			responseIntent.setPage( intent.getPageIndex() );
//		}
//		catch ( ClassCastException e ) {
//			Log.e( "NetworkSession", e.getMessage() );
//		}		
//
//		if ( !result ) {
//			responseIntent.setError( errorNo );
//			
//			if ( errorNo == ResponseIntent.ERROR_WITH_MESSAGE ) {
//				responseIntent.setMessage( errorMessage );
//			}
//		}
//		
//		return responseIntent;
//	}
//
//	private void sendResultIntent( RequestIntent reqIntent, boolean result, int errorNo, String errorMessage ) {
//		if ( reqIntent != null ) {
//			if ( isDestinationActivityOnTop( reqIntent.getSourceClass() ) || 
//			     reqIntent.getAction().equals( RequestIntent.request_login ) ) {
//				ResponseIntent responseIntent = createResponseIntent( reqIntent, result, errorNo, errorMessage );
//				mContext.startActivity( responseIntent );
//			}
//		}
//	}
	
//	private boolean isDestinationActivityOnTop( String destName ) {
//		boolean found = false;
//		ActivityManager activityManager = (ActivityManager) mContext.getApplicationContext().getSystemService( Context.ACTIVITY_SERVICE );
//        List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks( 50 );
//	    
//        //Log.d( "NetworkSession", "package name: " + mContext.getPackageName() );
//        
//        for ( RunningTaskInfo task : runningTasks  ) {
//        	String activityName = task.topActivity.getClassName();
//        	//Log.d( "NetworkSession", "top activity: " + activityName );
//        	
//        	if ( activityName.contains( mContext.getPackageName() ) ) {
//        		found = activityName.equals( destName );
//        		//Log.d( activityName, destName );
//        		//Log.d( "NetworkSession", "package found. activity found: " + String.valueOf( found ) );
//        		break;
//        	}
//        }
//        
//        return found;
//	}
	
	////////////////////
	// Network Service
	////////////////////
	
	public final NetworkService getNetworkService() {
    	return mNetworkService;
    }
}