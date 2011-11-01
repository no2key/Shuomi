package com.android.shuomi.groupon;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.shuomi.EnhancedEditText;
import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.EnhancedEditText.OnEditorActionDoneListener;
import com.android.shuomi.UI;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.util.Util;

import android.content.Context;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class GrouponMainView extends LinearLayout  {
	
	static final public int GROUPON_LIST_ITEM[] = { 
		R.string.local_cate, R.string.travel_hotel, R.string.online_shopping, 
		R.string.entertainment, R.string.beauty_healthy, R.string.others };
	
	private final String[] mMapKeys = { "label_key" };
	private final int[] mMapValues = { R.id.text_groupon_item };
	private String mProvince = null;
	private String mCity = null;
	private ListView mCityList = null;
	
	public GrouponMainView( Context context, String province, String city ) {
		super( context );
		
		mProvince = province;
		mCity = city;
		
		inflateLayout();
		createCityList();
		createGrouponList();
		registerEditorListener();
	}

	private void registerEditorListener() 
	{
		final EnhancedEditText edit = (EnhancedEditText) findViewById( R.id.edit_search );
		edit.setLeftDrawable( R.drawable.ic_search_small );
		edit.setOnEditorDoneListener( EditorInfo.IME_ACTION_SEARCH, new OnEditorActionDoneListener() 
		{
			@Override
			public void done() 
			{
				searchByKeyword( edit.getText().toString() );				
			}
		} );
	}
	
	private void searchByKeyword( String keyword )
	{
		if ( Util.isValid( keyword ) )
		{
			Bundle bundle = new Bundle();
			bundle.putString( REQUEST.PARAM_PROVINCE, mProvince );
			bundle.putString( REQUEST.PARAM_CITY, mCity );
			bundle.putString( REQUEST.PARAM_TITLE, keyword );
			
	    	goToNextView( new GrouponListView( getContext(), bundle ) );
		}
	}

	private void inflateLayout() {
		LayoutInflater layoutInflater = ( LayoutInflater )getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		layoutInflater.inflate( R.layout.groupon_main, this, true );
		mCityList = ( ListView ) findViewById( R.id.list_city );
		mCityList.getBackground().setAlpha( UI.VIEW_DEFAULT_ALPHA );
	}
	
	public void setLocation( String province, String city ) {
		mProvince = province;
		mCity = city;
		updateCityInList();
	}
	
	private void updateCityInList() {
		String cityLabel = getContext().getString( R.string.select_city );
		
		if ( Util.isValid( mCity ) ) {
			cityLabel += " - " + mCity;
		}
		
		String cityItems[] = { cityLabel };
		
		SimpleAdapter adapter = new SimpleAdapter ( getContext(), getData( cityItems ), 
				R.layout.single_field_item, mMapKeys, mMapValues );
		mCityList.setAdapter( adapter );
		adapter.notifyDataSetChanged();
	}
	
	private void createCityList() {
		updateCityInList();
		
		mCityList.setOnItemClickListener( new OnItemClickListener() {                            
			  
		    public void onItemClick( AdapterView<?> arg0, View arg1, int arg2,  long arg3 ) { 
		    	goToNextView( new GrouponProvinceListView( getContext() ) );
		    }
		} );
	}
	
	private void goToNextView( View view ) {
		if ( view != null ) {
			( ( ServiceListView ) getContext() ).goToNextView( view );
		}
	}
	
	private String[] getListItemLabels() {
		String[] items = new String[GROUPON_LIST_ITEM.length];
		
		for ( int i = 0; i < GROUPON_LIST_ITEM.length; i ++ ) {
			items[i] = getContext().getString( GROUPON_LIST_ITEM[i] );
		}
		
		return items;
	}
	
	private Bundle getParamBundle( int position ) {
		Bundle bundle = new Bundle();
		bundle.putString( REQUEST.PARAM_PROVINCE, mProvince );
		bundle.putString( REQUEST.PARAM_CITY, mCity );
		bundle.putString( REQUEST.PARAM_CATE, getContext().getString( GROUPON_LIST_ITEM[position] ) );
		return bundle;
	}
	
	private void createGrouponList() {
		ListView grouponListView = ( ListView ) findViewById( R.id.list_groupon );
		grouponListView.getBackground().setAlpha( UI.VIEW_DEFAULT_ALPHA );
		
		SimpleAdapter adpter = new SimpleAdapter ( getContext(), getData( getListItemLabels() ), 
				R.layout.single_field_item, mMapKeys, mMapValues );
		grouponListView.setAdapter( adpter );
		
		grouponListView.setOnItemClickListener( new OnItemClickListener() {                            
			  
		    public void onItemClick( AdapterView<?> arg0, View arg1, int arg2,  long arg3 ) { 
		    	Bundle bundle = getParamBundle( arg2 );
		    	goToNextView( new GrouponListView( getContext(), bundle ) );
		    }
		} );
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
}
