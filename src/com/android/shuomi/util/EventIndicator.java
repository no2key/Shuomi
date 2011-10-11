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
    
    static public void showToast( Context context, int error ) {
    	int resId = getResIdByError( error );
    	
    	if ( resId != -1 ) {
    		String prompt = context.getResources().getString( resId );
    		showToast( context, prompt );
    	}
    }
    
    static public void showToast( Context context, String prompt ) {
    	Toast.makeText( context, prompt, Toast.LENGTH_SHORT ).show();
    }
    
    static public void showAlert( Context context, int error, String message ) {
    	AlertDialog.Builder builder = new AlertDialog.Builder( context );
    	String prompt = message;
    	
    	if ( prompt == null || prompt.length() == 0 ) {
    		prompt = context.getString( getResIdByError( error ) );
    	}
    	
    	builder.setTitle( R.string.app_error )
    		   .setMessage( prompt ) 
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
