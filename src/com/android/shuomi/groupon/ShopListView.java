package com.android.shuomi.groupon;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.shuomi.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ShopListView extends LinearLayout {
	
	static final protected String SHOP_NAME = "shop_name";
	static final protected String SHOP_ADDR = "shop_addr";
	
	private final String[] mMapKeys = { SHOP_NAME, SHOP_ADDR };
	private final int[] mMapValues = { R.id.shop_name, R.id.shop_address };
	
	private ArrayList<String[]> mDataList = null;
	private ListView mList = null;
	
	private RadioGroup mRadioGroup = null;
	
	public ShopListView( Context context, ArrayList<String[]> list ) {
		super( context );
		inflateLayout();
		mDataList = list;
		fillList();
	}
	
	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.shop_list_view, this, true );
	}

	private void fillList() {
		if ( mDataList != null && mDataList.size() > 0 ) {
			SimpleAdapter adapter = new SimpleAdapter( getContext(), getData( mDataList ), 
					R.layout.shop_list_item, mMapKeys, mMapValues );
			mList = (ListView) findViewById( R.id.list_shop );
			mList.setAdapter( adapter );
			
			mList.setOnItemClickListener( new OnItemClickListener() {

				@Override
				public void onItemClick( AdapterView<?> arg0, View arg1, int position, long id ) {
					dialToShop( position );
				}				
			});
		}
	}
	
	private void dialToShop( int position ) {
		ArrayList<String> addressList = getAddressList( mDataList.get(position)[2] );
		
		for ( int i = 0; i < addressList.size(); i ++ ) {
			Log.d( "dialToShop", addressList.get(i) );
		}
		
		if ( addressList != null ) {
			if ( addressList.size() > 0 ) {
				showDialPrompt( addressList );
			}
		} 
	}
	
	private ArrayList<String> getAddressList( String address ) {
		int start = 0;
		int end = Integer.MAX_VALUE;
		ArrayList<String> list = new ArrayList<String>();
		char[] addrByte = address.toCharArray();
		
		while ( end > start ) {
			end = address.indexOf( '/', start );
			
			if ( end == -1 ) {
				list.add( new String( addrByte, start, addrByte.length - start ) );
			}
			else {
				list.add( new String( addrByte, start, end - start ) );
				start = end + 1;
				end = Integer.MAX_VALUE;
			}
		}
			
		return list;
	}
	
	private LinearLayout createTelSelectLayout( ArrayList<String> addressList ) {
		LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		LinearLayout layout = new LinearLayout( getContext() );  
        layout.setOrientation( LinearLayout.VERTICAL );
        
        TextView prompt = new TextView( getContext() );
        prompt.setText( R.string.prompt_select_number );
        prompt.setTextSize( 20 );
        prompt.setLayoutParams( LP_WW );
        prompt.setGravity( Gravity.LEFT | Gravity.CENTER_VERTICAL );
        prompt.setPadding( 10, 10, 10, 10 );
        layout.addView( prompt );
        
        RadioGroup numberGroup = new RadioGroup( getContext() );
        numberGroup.setLayoutParams( LP_WW );
        numberGroup.setPadding( 10, 5, 10, 10 );
        
        if ( addressList != null && addressList.size() > 0 ) {
        	for ( int i = 0; i < addressList.size(); i ++ ) {
        		RadioButton radio = new RadioButton( getContext() );
        		radio.setText( addressList.get(i) );
        		radio.setTextSize( 18 );
        		radio.setLayoutParams( LP_WW );
        		radio.setGravity( Gravity.CENTER | Gravity.CENTER_VERTICAL );
        		radio.setPadding( 40, 10, 10, 10 );
        		radio.setId( i );
        		
        		if ( i == 0 ) {
        			radio.setChecked( true );
        		}
        		
        		numberGroup.addView( radio );
        	}
        }
                
    	layout.addView( numberGroup );
    	mRadioGroup = numberGroup;
    	
    	Log.d( "check", "checked id = " + numberGroup.getCheckedRadioButtonId() );
        
        return layout;
	}
	
	private void showDialPrompt( final ArrayList<String> numList ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
    	
    	builder.setCancelable( true ) 
    	       .setNegativeButton( R.string.dlg_btn_cancel, new DialogInterface.OnClickListener() {				
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
    	
    	DialogInterface.OnClickListener listener = null;
    	
    	if ( numList.size() == 1 ) {
    		String prompt = getContext().getString( R.string.prompt_call_shop ) + "\r\n" + numList.get(0);
    		builder.setMessage( prompt );
    		
    		listener = new DialogInterface.OnClickListener() { 
    			public void onClick( DialogInterface dialog, int id ) { 
    				dialog.dismiss();
    				onSingleNumberOk( numList.get(0) );
    			} 
	        };
    	}
    	else {
    		builder.setView( createTelSelectLayout( numList ) );
    		listener = new DialogInterface.OnClickListener() { 
    			public void onClick( DialogInterface dialog, int id ) { 
    				dialog.dismiss();
    				if ( mRadioGroup != null ) {
    					onSingleNumberOk( numList.get( mRadioGroup.getCheckedRadioButtonId() ) );
    				}
    			} 
	        };
    	}
    	
    	builder.setPositiveButton( R.string.dlg_btn_ok, listener );
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	private void onSingleNumberOk( String number ) {
		Log.d( "dial", number );
		launchPhoneCall( getContext(), number );
	}
	
	private ArrayList< HashMap<String, Object> > getData( ArrayList<String[]> list ) {  
        ArrayList< HashMap<String, Object> > arrayList = new ArrayList< HashMap<String,Object> >();
        
        for ( int i = 0; i < list.size(); i ++ ) {  
            HashMap <String, Object> map = new HashMap< String, Object >();
            
            String[] fields = list.get( i );            
            map.put( SHOP_NAME, fields[0] );
            map.put( SHOP_ADDR, fields[1] );
            
            arrayList.add( map );
        }
          
        return arrayList;
    }
	
	private void launchPhoneCall( Context context, String phoneNumber ) {
		Intent dial = new Intent( "android.intent.action.CALL", Uri.parse( "tel:" + phoneNumber ) );
		context.startActivity( dial );
    }

}
