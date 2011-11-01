package com.android.shuomi.favorites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.UI;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.persistence.DatabaseSession;
import com.android.shuomi.util.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FavoritesListView extends LinearLayout {

	private static final String TAG = "FavoritesListView";
	private ListView mList = null;
	private ArrayList< HashMap<String, Object> > mItemDataList = new ArrayList< HashMap<String,Object> >();
	private SimpleAdapter mAdapter = null;
	private ArrayList<String[]> mDbDataList = null;
	
	static final private int mItemWidgetResIds[] = {
		R.id.provider, R.id.discount, R.id.price, R.id.details };
	static final private String KEY_PROVIDER = "KEY_PROVIDER";
	static final private String KEY_DISCOUNT = "KEY_DISCOUNT";
	static final private String KEY_PRICE = "KEY_PRICE";
	static final private String KEY_DETAILS = "KEY_DETAILS";
	
	static final private String[] mMapKeys = {
		KEY_PROVIDER, KEY_DISCOUNT, KEY_PRICE, KEY_DETAILS };
	
	private Observer mObserver = new Observer() {

		@Override
		public void update( Observable observable, Object data ) {
			getDataFromDb();
			mAdapter.notifyDataSetChanged();
			Log.d( "FavoritesListView", "new favorite added " + mAdapter.getCount() );
		}
	}; 
	
	public FavoritesListView(Context context) {
		super(context);
		
		inflateLayout();
		setupTitle();
		
		getDataFromDb();
		initList();
		registerItemClickListener();
		registerDbRecordAdded( mObserver );
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.favorites_list, this, true );
	}
	
	private void initList() {
		mList = (ListView) findViewById( R.id.list );
		mList.getBackground().setAlpha( UI.VIEW_DEFAULT_ALPHA );
		mList.setEmptyView( findViewById( R.id.text_no_item ) );
		
		mAdapter = new SimpleAdapter ( getContext(), mItemDataList, 
				R.layout.favorites_list_item, mMapKeys, mItemWidgetResIds );
		mList.setAdapter( mAdapter );
	}
	
	private void setupTitle()
	{
		TextView title = ( TextView ) findViewById( R.id.titlebar_label );
		title.setText( R.string.my_favourite );
		findViewById( R.id.titlebar_button ).setVisibility( View.GONE );
	}
	
	private void getDataFromDb() {
		final String[] columns = { /*RESPONSE.PARAM_IMG_2,*/ 
				RESPONSE.PARAM_PROVIDER, RESPONSE.PARAM_PRICE, RESPONSE.PARAM_ACTUAL_PRICE, 
				RESPONSE.PARAM_TITLE, RESPONSE.PARAM_EXPIRY, RESPONSE.PARAM_FOLLOWED, 
				RESPONSE.PARAM_ID, RESPONSE.PARAM_URL, RESPONSE.PARAM_SHOPLIST, 
				RESPONSE.PARAM_TIMESTAMP, BaseColumns._ID };
		
		Log.d( TAG, "start read db" );
		mDbDataList = DatabaseSession.getInstance().loadFavoriteRecords( columns, "timestamp desc" );
		Log.d( TAG, "end read db" );
		getAdapterDataList();
	}
	
	private void getAdapterDataList() 
	{
		if ( mDbDataList != null && mDbDataList.size() > 0 ) 
		{
			if ( !mItemDataList.isEmpty() )
			{
				mItemDataList.clear();
			}
			
			for ( int i = 0; i < mDbDataList.size(); i ++ ) 
			{  
	            HashMap <String, Object> map = new HashMap< String, Object >();
	            
	            String[] fields = mDbDataList.get( i );
	            map.put( KEY_PROVIDER, fields[0] );
	            map.put( KEY_DISCOUNT, Util.getDiscount( fields[2], fields[1] ) + getContext().getString( R.string.discount_symbol ) );
	            map.put( KEY_PRICE, getContext().getString( R.string.chinese_yuan_symbol ) + fields[2] );
	            map.put( KEY_DETAILS, fields[3] );
	            
	            mItemDataList.add( map );
	        }
		}		
	}
	
	private void registerDbRecordAdded( Observer observer ) {
		DatabaseSession.getInstance().registerRecordAddObserver( observer );
	}
	
	private void registerItemClickListener() {
		mList.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
				( ( ServiceListView ) getContext() ).goToNextView( new FavoriteDetailsView( getContext(), mDbDataList.get( position ) ) );
			}
			
		} );
		
		mList.setOnItemLongClickListener( new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id ) {
				showDeletePrompt( position );
				return false;
			}
			
		} );
	}
	
	private void showDeletePrompt( final int position ) {
		LayoutInflater inflater = LayoutInflater.from( getContext() );
		View confirm = inflater.inflate( R.layout.delete_email_confirm_dialog, null );
		confirm.findViewById( R.id.email_address ).setVisibility( View.GONE );
		TextView prompt = (TextView) confirm.findViewById( R.id.prompt );
		prompt.setText( R.string.prompt_delete_favorite );

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
	
	private void removeListItem( int position ) 
	{
		String dbId = mDbDataList.get( position )[10];
		DatabaseSession.getInstance().deleteFavoriteRecord( BaseColumns._ID, dbId );
		
		if ( mItemDataList != null && mItemDataList.size() > position ) {
			mItemDataList.remove( position );
			mAdapter.notifyDataSetChanged();
		}
		
		if ( mDbDataList != null && mDbDataList.size() > position ) {
			mDbDataList.remove( position );
		}
	}
}
