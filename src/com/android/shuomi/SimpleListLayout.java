package com.android.shuomi;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.shuomi.groupon.ListLayout;
import com.android.shuomi.parser.ResponseParser;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

public abstract class SimpleListLayout extends ListLayout {

	static final protected String LABEL_KEY = "label_key";
	
	protected final String[] mMapKeys = { LABEL_KEY };
	protected final int[] mMapValues = { R.id.text_groupon_item };
	protected final int mItemResId = R.layout.single_field_item;
	
	public SimpleListLayout( Context context ) {
		super( context );
	}
	
	protected void updateSimpleList( String[] items ) {
		if ( Util.isValid( items ) ) {
			SimpleAdapter adapter = new SimpleAdapter ( getContext(), getData( items ), 
					mItemResId, mMapKeys, mMapValues );
			setSimpleListAdapter( adapter );
		}		
	}
	
	protected void setSimpleListAdapter( BaseAdapter adapter ) {
		getList().setAdapter( adapter );
		adapter.notifyDataSetChanged();
	}
	
	private ArrayList< HashMap<String, Object> > getData( String[] items ) {  
        ArrayList< HashMap<String, Object> > arrayList = new ArrayList< HashMap<String,Object> >();
        
        for ( int i = 0; i < items.length; i ++ ) {  
            HashMap <String, Object> map = new HashMap< String, Object >();  
            map.put( "label_key", items[i] );
            arrayList.add( map );
        }
          
        return arrayList;
    }
	
	protected void updateArrayList( int page, ArrayList<String[]> itemList ) {
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void update( String requestAction, int dataType, Object data, int pageCount ) {
		if ( requestAction.equals( getRequestAction() ) ) {
			switch ( dataType ) {
			
			case ResponseParser.TYPE_ARRAY:
				updateSimpleList( (String[]) data );
				break;
				
			case ResponseParser.TYPE_ARRAY_LIST:
				updateArrayList( pageCount, (ArrayList<String[]>)data );
				break;
				
			default:
				break;
			}
		}
	}
}
