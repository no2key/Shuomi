package com.android.shuomi.groupon;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.shuomi.EnhancedEditText;
import com.android.shuomi.EnhancedEditText.OnAfterTextChangedListener;
import com.android.shuomi.EnhancedEditText.OnEditorActionDoneListener;
import com.android.shuomi.EnhancedEditText.OnRightDrawableClickListener;
import com.android.shuomi.R;
import com.android.shuomi.intent.GrouponSharedByEmailRequestIntent;
import com.android.shuomi.network.NetworkSession;
import com.android.shuomi.util.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class GrouponSharedByEmailView extends LinearLayout {

	static private final String TAG = "GrouponSharedByEmailView";
	private String MAP_VALUE = "EMAIL_ADDRESS";
	private String mItemId;
	private ListView mList;
	private EnhancedEditText mEdit;
	private ArrayList< HashMap<String, Object> > mArrayList = null;
	private SimpleAdapter mAdapter = null;
	
	public GrouponSharedByEmailView( Context context, String id ) {
		super(context);
		mItemId = id;
		inflateLayout();
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.shared_by_email, this, true );
		
		mList = (ListView) findViewById( R.id.list_address );
		
		mList.setOnItemLongClickListener( new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
				showDeletePrompt( position );
				return false;
			}
			
		} );
		
		mEdit = ( EnhancedEditText ) findViewById( R.id.edit_address );
		addEditTextListeners();
		
		Button send = (Button) findViewById( R.id.btn_send );
		send.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendEmail();				
			}
			
		} );
	}
	
	private void showDeletePrompt( final int position ) {
		LayoutInflater inflater = LayoutInflater.from( getContext() );
		View confirm = inflater.inflate( R.layout.delete_email_confirm_dialog, null );
		TextView address = (TextView) confirm.findViewById( R.id.email_address );

		if ( mArrayList != null & mArrayList.size() > position ) {
			HashMap <String, Object> map = mArrayList.get( position );
			address.setText( (String) map.get( MAP_VALUE ) );
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
		//builder.setIcon(android.R.drawable.ic_dialog_alert );
		//builder.setTitle( R.string.please_confirm );
		builder.setView( confirm );    	

    	builder.setNegativeButton( R.string.dlg_btn_cancel, new DialogInterface.OnClickListener() {				
			public void onClick( DialogInterface dialog, int which ) {
				dialog.dismiss();
			}
		});
    	builder.setPositiveButton( R.string.dlg_btn_ok, new DialogInterface.OnClickListener() {				
			public void onClick( DialogInterface dialog, int which ) {
				removeListItem( position );
				dialog.dismiss();				
			}
		});
    	
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	private void addEditTextListeners() {
		mEdit.setOnEditorDoneListener( new OnEditorActionDoneListener() {
			
			@Override
			public void done() {
				addListItem();				
			}
			
		} );
		
		mEdit.setOnAfterTextChangedListener( new OnAfterTextChangedListener() {

			@Override
			public void onChanged() {
				if ( Util.isValid( mEdit.getText().toString() ) ) {
					mEdit.setCompoundDrawablesWithIntrinsicBounds( android.R.drawable.sym_action_email, 
							0, R.drawable.ic_cancel, 0 );
				}
				else {
					mEdit.setCompoundDrawablesWithIntrinsicBounds( android.R.drawable.sym_action_email, 
							0, 0, 0 );
				}
			}
			
		} );
		
		mEdit.setOnRightDrawableClickListener( new OnRightDrawableClickListener() {

			@Override
			public void onClick() {
				mEdit.setText( "" );
			}
			
		});
	}
	
	private void addListItem() {
		String address = mEdit.getText().toString();
		
		if ( Util.isValid( address ) ) {
			if ( mArrayList == null ) {
				mArrayList = new ArrayList< HashMap<String, Object> >();
				mAdapter = new SimpleAdapter ( getContext(), mArrayList, R.layout.email_list_item, 
						new String[] { MAP_VALUE }, new int[] { R.id.text_email_address } );
				mList.setAdapter( mAdapter );
			}
			
			HashMap <String, Object> map = new HashMap< String, Object >();  
	        map.put( MAP_VALUE, address );
	        mArrayList.add( 0, map );
	        
	        mAdapter.notifyDataSetChanged();
		}
	}
	
	private void removeListItem( int position ) {
		if ( mArrayList != null && mArrayList.size() > position ) {
			mArrayList.remove( position );
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private void sendEmail() {
		String address = getEmailAddress();
		Log.d( TAG, "email address: " + address );
		
		if ( Util.isValid( address ) ) {
			NetworkSession.send( new GrouponSharedByEmailRequestIntent( getClass().getName(), mItemId, address ) );
		}
	}
	
	private String getEmailAddress() {
		String address = "";
		
		for ( int i = 0; i < mArrayList.size(); i ++ ) {
			HashMap <String, Object> map = mArrayList.get(i);
			address += map.get( MAP_VALUE );
			if ( i != mArrayList.size() - 1 ) {
				address += ";";
			}
		}
		
		return address;
	}
}
