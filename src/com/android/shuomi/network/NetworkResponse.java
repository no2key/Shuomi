package com.android.shuomi.network;

import com.android.shuomi.intent.ResponseIntent;

public class NetworkResponse {
	
	static public void process( NetworkResponseHandler handler, ResponseIntent response ) {
		handler.onResponse( response );
		
		if ( response.getResult() ) {
			handler.onPositiveResponse( response );
		}
		else {
			handler.onNegativeResponse( response.getError(), response.getUserData() );
		}
    }
}
