package com.android.shuomi.intent;

public class GrouponFavoriteRequestIntent extends RequestIntent {

	public GrouponFavoriteRequestIntent( String className, String itemId ) {
		super( REQUEST.ADD_TO_FAVORITE );
		
		setItemId( itemId );

		setSourceClass( className );
		setResponseAction( RESPONSE.HTTP_RESPONSE_GROUPON_FAVORITE );
	}
	
	public void setItemId( String id ) {
		putExtra( REQUEST.PARAM_ID, id );
	}
	
	public String getItemId() {
		return getStringExtra( REQUEST.PARAM_ID );
	}
}
