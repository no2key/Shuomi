package com.android.shuomi.persistence;

import android.content.Context;
import android.util.Log;

public class DatabaseSession {

	static final private String TAG = "DatabaseSession";
	
	
	static protected DatabaseSession mThis = null;
	
	static public DatabaseSession create( Context context ) {
		if ( mThis == null ) {
			mThis = new DatabaseSession( context );
		}
		
		return mThis;
	}
	
	static public DatabaseSession getInstance() 
	{
		if ( mThis == null ) 
		{
			Log.e( TAG, "null database session instance" );
		}
		
		return mThis;
	}
	
	protected ServiceDb mDatabase = null;
	
	public DatabaseSession() {}
	
	public DatabaseSession( Context context ) {
		mDatabase = new ServiceDb( context );
	}
}
