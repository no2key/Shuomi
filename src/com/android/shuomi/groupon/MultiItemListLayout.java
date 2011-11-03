package com.android.shuomi.groupon;

import java.util.ArrayList;

import com.android.shuomi.R;
import com.android.shuomi.ServiceListView;
import com.android.shuomi.intent.RESPONSE;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;


public abstract class MultiItemListLayout extends ListLayout {

	//private final String TAG = " MultiItemLayout ";
	private final int[] mItemFieldResIds = { R.id.demo_image, R.id.provider, R.id.discount, R.id.price, R.id.details };
	private final int mItemResId = R.layout.groupon_list_item;
	
	private ArrayList<ListItemUnion[]> mDataList = null;
	private BaseAdapter mAdapter = null;
	
	public MultiItemListLayout(Context context) {
		super( context );
	}
	
	@Override
	protected void onItemSingleClick( View view, int position, long rowId ) 
	{
		if ( mAdapter != null && position - getList().getHeaderViewsCount() < mAdapter.getCount() )
		{
			ListItemUnion[] item = (ListItemUnion[]) mAdapter.getItem( position - getList().getHeaderViewsCount() );
			
			if ( Util.isValid( item ) )
			{
				String id = item[item.length-1].getString();
				( ( ServiceListView ) getContext() ).goToNextView
					( new GrouponDetailsView( getContext(), id, RESPONSE.HTTP_RESPONSE_GROUPON ) );
			}
		}
	}
	
	protected void appendListItems( ArrayList<String[]> itemList )
	{
		if ( itemList != null ) 
		{
			if ( mDataList == null ) 
			{
				mDataList = new ArrayList<ListItemUnion[]>();
			}
			
			appendToDataList( itemList );
			
			if ( mAdapter == null ) 
			{
				mAdapter = new MiscListAdapter( getContext(), mItemResId, mDataList, null, null, mItemFieldResIds );
				getList().setAdapter( mAdapter );
			}
			
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private void appendToDataList( ArrayList<String[]> fieldsList ) 
	{
		if ( mDataList != null && fieldsList != null ) 
		{
			for ( int i = 0; i < fieldsList.size(); i ++ ) 
			{
				String[] fields = fieldsList.get( i );
				fields = convertFields( fields );
				ListItemUnion[] items = getListItemUnion( fields );
				mDataList.add( items );
			}
		}
	}
	
	private String[] convertFields( String[] fields ) 
	{		
		String discount = Util.getDiscount( fields[3] , fields[2] );
		
		if ( Util.isValid( discount ) ) 
		{
			fields[2] = discount + getContext().getString( R.string.discount_symbol );
		}
		else 
		{
			fields[2] = "";
		}
		
		fields[3] = getContext().getString( R.string.chinese_yuan_symbol ) + fields[3];
		
		return fields;
	}
	
	private ListItemUnion[] getListItemUnion( String[] fields ) 
	{
		ListItemUnion[] items = null;
		
		if ( Util.isValid( fields ) ) 
		{
			items = new ListItemUnion[ fields.length ];
			
			if ( items != null ) 
			{
				// the 1st field is for image URI
				items[0] = new ListItemUnion( R.drawable.spinner_black_16, fields[0] );
				
				for ( int i = 1; i < fields.length; i ++ ) 
				{
					items[i] = new ListItemUnion( fields[i] );
				}
			}
		}
		
		return items;
	}
	
	protected BaseAdapter getAdapter() {
		return mAdapter;
	}
}
