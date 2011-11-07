package com.android.shuomi.persistence;

import android.content.Context;
import android.util.Log;

public class DatabaseSession {

	static final private String TAG = "DatabaseSessionEx";
	static private DatabaseSession mThis = null;
	
	@SuppressWarnings("unchecked")
	static public <T extends DatabaseSession> T create( Context context ) {
		if ( mThis == null ) {
			mThis = new DatabaseSession( context );
		}
		
		return (T) mThis;
	}
	
	@SuppressWarnings("unchecked")
	static public <T extends DatabaseSession> T getInstance() 
	{
		if ( mThis == null ) 
		{
			Log.e( TAG, "null database session instance" );
		}
		
		return (T) mThis;
	}
	
	protected ServiceDb mDatabase = null;
	
	public DatabaseSession( Context context ) {
		mDatabase = new ServiceDb( context );
	}	
}
