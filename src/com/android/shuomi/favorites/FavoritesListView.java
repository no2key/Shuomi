package com.android.shuomi.favorites;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.shuomi.R;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.persistence.DatabaseSession;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FavoritesListView extends LinearLayout {

	private ListView mList = null;
	private ArrayList< HashMap<String, Object> > mDataList = null;
	private SimpleAdapter mAdapter = null;
	
	static final private int mItemWidgetResIds[] = {
		R.id.provider, R.id.discount, R.id.price, R.id.details };
	static final private String KEY_PROVIDER = "KEY_PROVIDER";
	static final private String KEY_DISCOUNT = "KEY_DISCOUNT";
	static final private String KEY_PRICE = "KEY_PRICE";
	static final private String KEY_DETAILS = "KEY_DETAILS";
	
	static final private String[] mMapKeys = {
		KEY_PROVIDER, KEY_DISCOUNT, KEY_PRICE, KEY_DETAILS };
	
	
	public FavoritesListView(Context context) {
		super(context);
		inflateLayout();
		getDataFromDb();
		fillList();
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.favorites_list, this, true );
		mList = (ListView) findViewById( R.id.list );
	}
	
	private void fillList() {
		if ( mDataList != null && mDataList.size() > 0 ) {
			mAdapter = new SimpleAdapter ( getContext(), mDataList, 
					R.layout.favorites_list_item, mMapKeys, mItemWidgetResIds );
			mList.setAdapter( mAdapter );
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private void getDataFromDb() {
		final String[] columns = { /*RESPONSE.PARAM_IMG_2,*/ 
				RESPONSE.PARAM_PROVIDER, RESPONSE.PARAM_PRICE, RESPONSE.PARAM_ACTUAL_PRICE, 
				RESPONSE.PARAM_TITLE, RESPONSE.PARAM_EXPIRY, RESPONSE.FOLLOWED, 
				RESPONSE.PARAM_ID, RESPONSE.PARAM_URL, RESPONSE.PARAM_SHOPLIST, 
				RESPONSE.PARAM_TIMESTAMP };
		
		ArrayList<String[]> dbList = DatabaseSession.getInstance().loadFavoriteRecords( columns, "timestamp desc" );
		mDataList = getDataList( dbList );
	}
	
	private ArrayList< HashMap<String, Object> > getDataList( ArrayList<String[]> list ) {  
        ArrayList< HashMap<String, Object> > arrayList = new ArrayList< HashMap<String,Object> >();
        
        for ( int i = 0; i < list.size(); i ++ ) {  
            HashMap <String, Object> map = new HashMap< String, Object >();
            
            String[] fields = list.get( i );
            map.put( KEY_PROVIDER, fields[0] );
            map.put( KEY_DISCOUNT, Util.getDiscount( fields[2], fields[1] ) );
            map.put( KEY_PRICE, fields[2] );
            map.put( KEY_DETAILS, fields[3] );
            
            arrayList.add( map );
        }
          
        return arrayList;
    }
}
