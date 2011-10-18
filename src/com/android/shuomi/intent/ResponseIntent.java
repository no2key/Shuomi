package com.android.shuomi.intent;

import android.content.Intent;

public class ResponseIntent extends Intent {
	
	static final public int UNKNOWN_ERROR = 1;
	
	public ResponseIntent( String action ) {
		super( action );
	}
	
	public ResponseIntent( Intent intent ) {
		super( intent );
	}

	public void setRequestAction( String action ) {
		putExtra( PARAM.SESSION_RSP.REQUEST, action );
	}
	
	public String getRequestAction() {
		return getStringExtra( PARAM.SESSION_RSP.REQUEST );
	}
	
	public void setResult( boolean result ) {
		putExtra( PARAM.SESSION_RSP.RESULT, result );
	}
	
	public boolean getResult() {
		return getBooleanExtra( PARAM.SESSION_RSP.RESULT, false );
	}
	
	public void setError( int error ) {
		putExtra( PARAM.SESSION_RSP.ERROR, error );
	}
	
	public int getError() {
		return getIntExtra( PARAM.SESSION_RSP.ERROR, UNKNOWN_ERROR );
	}
	
	public void setUserData( String data ) {
		putExtra( PARAM.SESSION_RSP.DATA, data );
	}
	
	public String getUserData() {
		return getStringExtra( PARAM.SESSION_RSP.DATA );
	}
	
	public void setSourceClass( String className ) {
		putExtra( PARAM.HTTP_RSP.SOURCE_CLASS, className );
	}
	
	public String getSourceClass() {
		return getStringExtra( PARAM.HTTP_RSP.SOURCE_CLASS );
	}
}
