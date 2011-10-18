package com.android.shuomi.network;

import com.android.shuomi.intent.PARAM;
import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.intent.ResponseIntent;

import android.os.Bundle;
import android.util.Log;

public class ResponseHandler {

	static final public int HTTP_OK = 200;
	static final public int HTTP_ABORT = 600;
	
	static public ResponseIntent process( Bundle bundle, RequestIntent request ) {
		int status = bundle.getInt( PARAM.HTTP_RSP.STATUS );
		ResponseIntent response = null;
		
		if ( request != null ) {
			response = new ResponseIntent( request.getResponseAction() );		
			response.setResult( status == HTTP_OK );
			response.setRequestAction( request.getAction() );
			response.setSourceClass( request.getSourceClass() );
			
			Log.d( "ResponseHandler", "request valid, status = " + status );
			
			if ( status == HTTP_OK ) {
				String body = bundle.getString( PARAM.HTTP_RSP.BODY );
				response.setUserData( body );
			}
			else {
				response.setError( status );
			}
		}
		else if ( status == HTTP_ABORT ) {
			Log.d( "ResponseHandler", "request NOT valid, status = " + status );
		}
		
		return response;
	}
}
