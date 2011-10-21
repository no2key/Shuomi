package com.android.shuomi.groupon;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.shuomi.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class GrouponSharedByEmailView extends LinearLayout {
	
	private String MAP_VALUE = "EMAIL_ADDRESS";
	private String mItemId;
	private ListView mList;
	
	public GrouponSharedByEmailView( Context context, String id ) {
		super(context);
		mItemId = id;
		inflateLayout();
		initList();
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.shared_by_email, this, true );
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
