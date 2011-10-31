package com.android.shuomi.groupon;

import com.android.shuomi.R;
import com.android.shuomi.intent.REQUEST;
import com.android.shuomi.util.Util;

import android.content.Context;
import android.os.Bundle;

public class GrouponListView extends MultiItemListLayout {

	private String mReqAction = REQUEST.LIST_GROUPON;
	private Bundle mBundle = null;
	
	public GrouponListView( Context context, Bundle bundle ) {
		super(context);

		mBundle = bundle;
		setupTitle();
		requestGrouponList( mBundle );
	}
	
	private void setupTitle() 
	{
		String title = "";
		String category = mBundle.getString( REQUEST.PARAM_CATE );
 
		if ( Util.isValid( category ) )
		{
			title = category;
		}
		else
		{
			title = getContext().getString( R.string.search );
		}
		
		String city = mBundle.getString( REQUEST.PARAM_CITY );
		
		if ( Util.isValid( city ) )
		{
			title += " - " + city;
		}
		
		String keyword = mBundle.getString( REQUEST.PARAM_TITLE );
		
		if ( Util.isValid( keyword ) )
		{
			title += " - " + keyword;
		}
		
		setTitleText( title );
		//setButtonText( R.string.hide_pic );
		enableButton( false );
	}

	@Override
	protected String getRequestAction() {
		return mReqAction;
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.groupon_list;
	}
}
