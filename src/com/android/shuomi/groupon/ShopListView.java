package com.android.shuomi.groupon;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.shuomi.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ShopListView extends LinearLayout {
	
	static final protected String SHOP_NAME = "shop_name";
	static final protected String SHOP_ADDR = "shop_addr";
	
	private final String[] mMapKeys = { SHOP_NAME, SHOP_ADDR };
	private final int[] mMapValues = { R.id.shop_name, R.id.shop_address };
	
	private ArrayList<String[]> mDataList = null;
	private ListView mList = null;
	
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
		String address = mDataList.get(position)[2];
		Log.d( "vvvv", address );
	}
	
	private void showDialPrompt() {
		AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
    	
    	builder//.setTitle( R.string.app_error )
    		   .setMessage( R.string.prompt_call_shop ) 
    	       .setCancelable( true ) 
    	       .setNegativeButton( R.string.dlg_btn_ok, new DialogInterface.OnClickListener() {				
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
    	       .setPositiveButton( R.string.dlg_btn_ok, new DialogInterface.OnClickListener() { 
    	           public void onClick( DialogInterface dialog, int id ) { 
    	        	   dialog.dismiss();
    	           } 
    	       });
    	
    	AlertDialog alert = builder.create();
    	alert.show();
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
}
