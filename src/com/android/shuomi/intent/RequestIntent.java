package com.android.shuomi.intent;

import android.content.Context;
import android.content.Intent;

public class RequestIntent extends Intent {
	
	public static final String RSP_ACTION = "RSP_ACTION";
	public static final String SOURCE_CLASS = "SOURCE_CLASS";
	public static final String PARAM_1 = "PARAM_1";
	
	public static final int GET = 0;
	public static final int POST = 1;
	
	public RequestIntent( String action, Context packageContext, Class<?> cls ) {
		super( packageContext, cls );
		setAction( action );
	}
	
	public RequestIntent( String action ) {
		super();
		setAction( action );
	}
	
	public void setResponseAction( String action ) {
		putExtra( RSP_ACTION, action );
	}
	
	public String getResponseAction() {
		return getStringExtra( RSP_ACTION );
	}
	
	public void setSourceClass( String className ) {
		putExtra( SOURCE_CLASS, className );
	}
	
	public String getSourceClass() {
		return getStringExtra( SOURCE_CLASS );
	}
	
	public void setParam1( String param ) {
		putExtra( PARAM_1, param );
	}
	
	public String getParam1() {
		return getStringExtra( PARAM_1 );
	}
}
