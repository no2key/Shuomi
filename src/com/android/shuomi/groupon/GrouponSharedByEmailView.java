package com.android.shuomi.groupon;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.shuomi.R;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class GrouponSharedByEmailView extends LinearLayout {
	
	private String MAP_VALUE = "EMAIL_ADDRESS";
	private String mItemId;
	private ListView mList;
	private EditText mEdit;
	private ArrayList< HashMap<String, Object> > mArrayList = null;
	private SimpleAdapter mAdapter = null;
	
	public GrouponSharedByEmailView( Context context, String id ) {
		super(context);
		mItemId = id;
		inflateLayout();
		//initList();
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.shared_by_email, this, true );
		
		mList = (ListView) findViewById( R.id.list_address );
		mEdit = (EditText) findViewById( R.id.edit_address );
		
		mEdit.setOnEditorActionListener( new OnEditorActionListener() {
			@Override
			public boolean onEditorAction( TextView view, int actionId, KeyEvent event ) {
				if ( actionId == EditorInfo.IME_ACTION_DONE ) {
					addListItem();
				}
				return false;
			}			
		} );
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

	private void initList() {
		mList = (ListView) findViewById( R.id.list_address );
		
		String[] emptyItems = { "", "" };
		SimpleAdapter adapter = new SimpleAdapter ( getContext(), getData( emptyItems ), 
				R.layout.email_list_item, new String[] { MAP_VALUE }, new int[] { R.id.text_email_address } );
		mList.setAdapter( adapter );
	}
	
	private ArrayList< HashMap<String, Object> > getData( String[] items ) {  
        ArrayList< HashMap<String, Object> > arrayList = new ArrayList< HashMap<String,Object> >();
        
        for ( int i = 0; i < items.length; i ++ ) {  
            HashMap <String, Object> map = new HashMap< String, Object >();  
            map.put( MAP_VALUE, items[i] );
            arrayList.add( map );
        }
          
        return arrayList;
    }
}
