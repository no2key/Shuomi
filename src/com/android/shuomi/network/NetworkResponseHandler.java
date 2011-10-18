package com.android.shuomi.network;

import com.android.shuomi.intent.ResponseIntent;

public interface NetworkResponseHandler {
	void onResponse( ResponseIntent response );
	void onPositiveResponse( ResponseIntent response );
	void onNegativeResponse( int error, String message );
}