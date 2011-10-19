package com.android.shuomi.intent;

public class GrouponDetailsRequestIntent extends RequestIntent {

	public GrouponDetailsRequestIntent( String action, String id ) {
		super(action);
		setItemId( id );
		setResponseAction( RESPONSE.HTTP_RESPONSE_GROUPON );
	}

	public void setItemId( String id ) {
		putExtra( REQUEST.PARAM_ID, id );
	}
	
	public String getItemId() {
		return getStringExtra( REQUEST.PARAM_ID );
	}
}
