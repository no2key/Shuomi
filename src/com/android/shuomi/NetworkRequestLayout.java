package com.android.shuomi;

import android.content.Context;
import android.widget.LinearLayout;

import com.android.shuomi.intent.RequestIntent;
import com.android.shuomi.network.NetworkSession;

public abstract class NetworkRequestLayout extends LinearLayout implements UpdatableView {

	private int mRequestIntentHashCode = 0;
	private boolean mRequestPending = false;
	
	public NetworkRequestLayout(Context context) {
		super(context);
	}
	
	@Override
	protected void onDetachedFromWindow () {
		cancelRequest();
	}
	
	public boolean isRequestPending() {
		return mRequestPending;
	}

	public boolean sendRequest( RequestIntent request ) {
		boolean accepted = !isRequestPending();
		
		if ( accepted ) {
			mRequestIntentHashCode = request.hashCode();
			mRequestPending = true;
			NetworkSession.send( request );
		}
		
		return accepted;
	}
	
	public void cancelRequest() {
		if ( isRequestPending() && mRequestIntentHashCode != 0 ) {
			NetworkSession.cancel( mRequestIntentHashCode );
		}
	}
	
	public void onRequestDone() {
		mRequestIntentHashCode = 0;
		mRequestPending = false;
	}
}
