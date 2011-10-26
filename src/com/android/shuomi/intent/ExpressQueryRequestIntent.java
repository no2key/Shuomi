package com.android.shuomi.intent;

public class ExpressQueryRequestIntent extends RequestIntent {

	public ExpressQueryRequestIntent( String className, String expressId, String orderId ) {
		super( REQUEST.EXPRESS_QUERY );
		setSourceClass( className );
		setResponseAction( RESPONSE.HTTP_RESPONSE_EXPRESS );
		
		setExpressId( expressId );
		setOrderNo( orderId );
	}

	public void setExpressId( String id ) {
		putExtra( REQUEST.PARAM_ID, id );
	}
	
	public String getExpressId() {
		return getStringExtra( REQUEST.PARAM_ID );
	}
	
	public void setOrderNo( String no ) {
		putExtra( REQUEST.PARAM_NO, no );
	}
	
	public String getOrderNo() {
		return getStringExtra( REQUEST.PARAM_NO );
	}
}
