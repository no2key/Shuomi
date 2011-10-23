package com.android.shuomi.intent;

public class GrouponSharedByEmailRequestIntent extends RequestIntent {

	public GrouponSharedByEmailRequestIntent( String className, String itemId, String address ) {
		super( REQUEST.SHARED_BY_EMAIL );
		
		setItemId( itemId );
		setEmailAddress( address );

		setSourceClass( className );
		setResponseAction( RESPONSE.HTTP_RESPONSE_GROUPON_SHARE );
	}

	public void setItemId( String id ) {
		putExtra( REQUEST.PARAM_ID, id );
	}
	
	public String getItemId() {
		return getStringExtra( REQUEST.PARAM_ID );
	}
	
	public void setEmailAddress( String address ) {
		putExtra( REQUEST.PARAM_EMAIL, address );
	}
	
	public String getEmailAddress() {
		return getStringExtra( REQUEST.PARAM_EMAIL );
	}
}
