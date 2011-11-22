package com.android.shuomi.util;

import com.android.shuomi.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class EventIndicator {
	
	static private int getResIdByError( int error ) {
    	int resId = -1;
    	
    	switch ( error ) 
    	{    		
    	default:
    		break;
    	}
    	
    	return resId;
    }
    
	static public void showToast( Context context, int resId )
	{
		Toast.makeText( context, resId, Toast.LENGTH_SHORT ).show();
	}
	
    static public void showToast( Context context, String prompt ) {
    	Toast.makeText( context, prompt, Toast.LENGTH_SHORT ).show();
    }
    
    static public void showAlert( Context context, int error, String message ) 
    {
    	String prompt = message;
    	
    	if ( !Util.isValid( prompt ) ) 
    	{
    		prompt = context.getString( getResIdByError( error ) );
    	}
    	
    	showAlert( context, prompt );
    }
    
    static public void showAlert( Context context, int resId ) 
    {
    	showAlert( context, context.getString( resId ) );
    }
    
    static public void showAlert( Context context, String message ) 
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder( context );

    	builder.setTitle( R.string.app_error )
    		   .setIcon( android.R.drawable.ic_dialog_alert )
    		   .setMessage( message ) 
    	       .setCancelable( false ) 
    	       .setPositiveButton( R.string.dlg_btn_ok, new DialogInterface.OnClickListener() { 
    	           public void onClick( DialogInterface dialog, int id ) { 
    	        	   dialog.dismiss();
    	           } 
    	       });
    	
    	AlertDialog alert = builder.create();
    	alert.show();
    }
}
