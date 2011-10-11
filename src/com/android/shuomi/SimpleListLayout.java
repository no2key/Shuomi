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

//	abstract protected String getRequestAction();
//	
//	private TextView mTitle = null;
//	private Button mButton = null;
//	private ListView mList = null;
	
	static final protected String LABEL_KEY = "label_key";
	
	private final String[] mMapKeys = { LABEL_KEY };
	private final int[] mMapValues = { R.id.text_groupon_item };
	private final int mItemResId = R.layout.single_field_item;
	
	public SimpleListLayout( Context context ) {
		super( context );
//		inflateLayout();
//		getChildWidgets();
	}
	
//	private void inflateLayout() {
//		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//		layoutInflater.inflate( R.layout.simple_list_with_titlebar, this, true );
//	}
//	
//	private void getChildWidgets() {
//		mTitle = ( TextView ) findViewById( R.id.titlebar_label );		
//		mButton = ( Button ) findViewById( R.id.titlebar_button );
//		mList = ( ListView ) findViewById( R.id.list );
//		
//		mList.setOnItemClickListener( new OnItemClickListener() {                            
//			  
//		    public void onItemClick( AdapterView<?> arg0, View arg1, int arg2,  long arg3 ) { 
//		    	onItemSingleClick( arg1, arg2, arg3 );
//		    }
//		} );
//	}
//	
//	protected void onItemSingleClick( View view, int position, long rowId ) {
//		// leave for derived class to implement
//	}
//	
//	protected void setTitleText( int id ) {
//		mTitle.setText( id );
//	}
//	
//	protected void setTitleText( String label ) {
//		mTitle.setText( label );
//	}
//	
//	protected void setButtonText( int id ) {
//		mButton.setText( id );
//	}
//	
//	protected void enableButton( boolean enable ) {
//		mButton.setVisibility( enable ? View.VISIBLE : View.GONE );
//	}
	
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
	
	protected void updateMultiPageList( int page, ArrayList<String[]> itemList ) {
		
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
				updateMultiPageList( pageCount, (ArrayList<String[]>)data );
				break;
				
			default:
				break;
			}
		}
	}
	
//	protected ListView getList() {
//		return mList;
//	}
}
