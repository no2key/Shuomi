package com.android.shuomi.network;

import com.android.shuomi.intent.PARAM;
import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.intent.ResponseIntent;

import android.os.Bundle;

public class ResponseHandler {

	static final private int HTTP_OK = 200;
	
	static public ResponseIntent process( Bundle bundle, RequestIntent request ) {
		int status = bundle.getInt( PARAM.HTTP_RSP.STATUS );
		
		ResponseIntent response = new ResponseIntent( request.getResponseAction() );		
		response.setResult( status == HTTP_OK );
		response.setRequestAction( request.getAction() );
		
		if ( status == HTTP_OK ) {
			String body = bundle.getString( PARAM.HTTP_RSP.BODY );
			response.setUserData( body );
		}
		else {
			response.setError( status );
		}
		
		return response;
	}
}
